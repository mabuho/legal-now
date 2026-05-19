export interface PaymentMethod {
    id: string
    type: PaymentMethodType
    cardNumber: string
    cardType: CardType
    expiryDate: string
    cardHolder: string
    isDefault: boolean
}

export enum PaymentMethodType {
    CREDIT = 'credit',
    DEBIT = 'debit'
}

export enum CardType {
    VISA = 'visa',
    MASTERCARD = 'mastercard',
    AMERICAN_EXPRESS = 'american_express',
    DISCOVER = 'discover'
}

export interface PaymentDetails {
    service: string
    professional: string
    date: string
    time: string
    amount: number
}