package com.lzh;

import com.lzh.mapper.AiMapper;
import com.lzh.mapper.LoginMapper;
import com.lzh.mapper.ProfileMapper;
import com.lzh.pojo.LoginInfo;
import com.lzh.pojo.PatientProfile;
import com.lzh.pojo.User;
import com.lzh.service.impl.AiServiceImpl;
import com.lzh.service.impl.LoginServiceImpl;
import com.lzh.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class AiSmartHealthDoctorApplicationTests {

    // ==== 核心业务 Service ====
    @InjectMocks
    private AiServiceImpl aiService;
    @Mock
    private AiMapper aiMapper;

    // ==== 常规业务 Service ====
    @InjectMocks
    private LoginServiceImpl loginService;
    @Mock
    private LoginMapper loginMapper;

    @InjectMocks
    private ProfileServiceImpl profileService;
    @Mock
    private ProfileMapper profileMapper;

    @BeforeEach
    void setUp() {
        // 初始化 Mockito 注解
        MockitoAnnotations.openMocks(this);

        // 为核心 AI 业务注入属性，防止 NPE
        ReflectionTestUtils.setField(aiService, "baseUrl", "http://mock-url");
        ReflectionTestUtils.setField(aiService, "apiKey", "mock-api-key");
        ReflectionTestUtils.setField(aiService, "model", "mock-model");

        // 由于 AiServiceImpl 中用到了 LoginMapper，手动注入
        ReflectionTestUtils.setField(aiService, "loginMapper", loginMapper);
    }

    @Test
    void contextLoads() {
        // 保证 Spring Boot 基础上下文加载正常
    }

    // ==========================================
    // 1. 核心业务 (AiServiceImpl) - 目标: 100% 覆盖
    // ==========================================

    @Test
    void testParseOpenAIStreamChunk() throws Exception {
        // 通过反射测试私有数据清洗方法
        Method parseMethod = AiServiceImpl.class.getDeclaredMethod("parseOpenAIStreamChunk", String.class);
        parseMethod.setAccessible(true);

        // 分支 1：null 或 包含 [DONE]
        assertEquals("", parseMethod.invoke(aiService, "[DONE]"));
        assertEquals("", parseMethod.invoke(aiService, (String) null));

        // 分支 2：正常的 JSON 流式返回
        String normalChunk = "data: {\"choices\": [{\"delta\": {\"content\": \"这是测试\"}}]}";
        assertEquals("这是测试", parseMethod.invoke(aiService, normalChunk));

        // 分支 3：空数据结构
        assertEquals("", parseMethod.invoke(aiService, "data: "));

        // 分支 4：非法 JSON 引发异常，走到 catch 块返回空字符串
        assertEquals("", parseMethod.invoke(aiService, "data: {bad_json}"));
    }

    @Test
    void testChatStreamWithHealthContext() {
        // 模拟查��患者档案
        PatientProfile profile = new PatientProfile();
        profile.setId(1);
        profile.setRealName("张三");
        profile.setAge(30);
        profile.setGender("男");
        profile.setMedicalHistory("高血压");
        when(loginMapper.getProfileById(anyInt())).thenReturn(profile);

        List<Map<String, String>> history = new ArrayList<>();

        try {
            // WebClient 虽然未被完全 mock 掉网络层，但参数组装部分(Profile转换、SystemMsg格式化)能正常走过
            aiService.chatStreamWithHealthContext(1, history);
        } catch (Exception e) {
            // 因 WebClient 访问假地址报错正常
        }

        // 验证患者档案是否正确被读取
        verify(loginMapper, times(1)).getProfileById(1);
    }

    @Test
    void testUpdateProfileHistory() {
        // 测试总结更新保存
        doNothing().when(aiMapper).updateProfileHistory(1, "病情总结");
        aiService.updateProfileHistory(1, "病情总结");
        verify(aiMapper, times(1)).updateProfileHistory(1, "病情总结");
    }

    // ==========================================
    // 2. 常规业务 (LoginServiceImpl) - 目标: >80% 覆盖
    // ==========================================

    @Test
    void testLoginSuccess() {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("testuser");
        when(loginMapper.login("testuser", "123456")).thenReturn(mockUser);

        LoginInfo result = loginService.login("testuser", "123456");

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("testuser", result.getUsername());
        assertNotNull(result.getToken()); // JwtUtils 生成 Token
    }

    @Test
    void testLoginFailure() {
        when(loginMapper.login(anyString(), anyString())).thenReturn(null);

        LoginInfo result = loginService.login("wrong", "wrong");

        assertNull(result); // 账号密码错误应返回 null
    }

    // ==========================================
    // 3. 常规业务 (ProfileServiceImpl) - 目标: >80% 覆盖
    // ==========================================

    @Test
    void testGetProfiles() {
        PatientProfile p1 = new PatientProfile(); p1.setId(1);
        PatientProfile p2 = new PatientProfile(); p2.setId(2);
        when(profileMapper.getProfilesByUserId(100)).thenReturn(Arrays.asList(p1, p2));

        List<PatientProfile> result = profileService.getProfiles(100);

        assertEquals(2, result.size());
        verify(profileMapper, times(1)).getProfilesByUserId(100);
    }

    @Test
    void testAddProfile() {
        PatientProfile p = new PatientProfile();
        p.setRealName("李四");
        doNothing().when(profileMapper).addProfile(p);

        profileService.addProfile(p);
        verify(profileMapper, times(1)).addProfile(p);
    }

    @Test
    void testUpdateProfile() {
        PatientProfile p = new PatientProfile();
        p.setId(1);
        doNothing().when(profileMapper).updateProfile(p);

        profileService.updateProfile(p);
        verify(profileMapper, times(1)).updateProfile(p);
    }
}