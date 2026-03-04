// src/types/index.ts
export interface PatientProfile {
    id?: number;
    userId?: number;
    realName: string;
    age: number;
    gender: string;
    relation: string;
    height?: number;
    weight?: number;
    medicalHistory?: string;
    aiSummary?: string;
}

export interface Result<T> {
    code: number;
    msg: string;
    data: T;
}

export interface User {
    username: string;
    password?: string;
}

export interface LoginInfo {
    id: number;
    username: string;
    token: string;
}
