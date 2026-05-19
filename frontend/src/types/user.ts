export interface User {
    name: string,
    email: string,
    password?: string,
    avatar: string,
    role: Role,
    roomId?: number
}

export type Role = 'lawyer' | 'client'

export interface UserProfile {
    name: string
    email: string
    phone: string
    address: string
    identification: string
    avatar?: string
  }