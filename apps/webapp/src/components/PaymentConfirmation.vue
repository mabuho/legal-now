<template>
    <template v-if="step === 1">
        <h2 class="text-2xl font-bold text-center mb-6">Confirm Payment</h2>
        <div class="mb-6 space-y-1">
            <div class="flex justify-between">
                <span>Service:</span>
                <span>{{ paymentDetails?.service }}</span>
            </div>
            <div class="flex justify-between">
                <span>Professional:</span>
                <span>{{ paymentDetails?.professional }}</span>
            </div>
            <div class="flex justify-between">
                <span>Date:</span>
                <span>{{ paymentDetails?.date }}</span>
            </div>
            <div class="flex justify-between">
                <span>Time:</span>
                <span>{{ paymentDetails?.time }}</span>
            </div>
            <div class="flex justify-between font-bold text-cyan-400 text-lg mt-2">
                <span>Amount:</span>
                <span>${{ paymentDetails?.amount?.toFixed(2) }}</span>
            </div>
        </div>
        <div class="mb-4">
            <div class="font-semibold mb-2">Select Payment Method</div>
            <div v-for="method in paymentMethods" :key="method.id"
                class="flex items-center mb-2 p-3 rounded-xl border cursor-pointer"
                :class="selectedMethod === method.id ? 'bg-cyan-900 border-cyan-600' : 'bg-slate-800 border-slate-700'"
                @click="selectedMethod = method.id">
                <input type="radio" :checked="selectedMethod === method.id" class="mr-3 accent-cyan-600" />
                <div>
                    <div class="font-semibold">
                        <span class="capitalize">{{ method.cardType }}</span>
                        ending in {{ method.cardNumber.slice(-4) }}
                    </div>
                    <div class="text-xs text-slate-400">
                        Expires {{ method.expiryDate }}
                    </div>
                </div>
            </div>
            <button class="w-full mt-2 text-cyan-400 hover:underline text-sm">
                Add New Payment Method
            </button>
        </div>
        <div class="flex gap-2 mt-6">
            <button @click="$emit('close')"
                class="flex-1 px-4 py-2 rounded-md bg-slate-700 hover:bg-slate-800 text-slate-200 font-semibold">Cancel</button>
            <button @click="pay" :disabled="!selectedMethod"
                class="flex-1 px-4 py-2 rounded-md bg-cyan-600 hover:bg-cyan-500 text-white font-semibold disabled:opacity-50">Pay
                Now</button>
        </div>
    </template>
    <AlertCard v-if="step === 2" title="Payment Successful!"
        message="Your payment for the service has been successfully processed." type="success"
        @close-alert="handleCloseAlert" />
    <AlertCard v-if="step === 3" title="Payment Failed"
        message="There was a problem processing your payment. Please try again or use a different method." type="error"
        @close-alert="handleCloseAlert" />
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { PaymentMethod } from '@/types/payment'
import AlertCard from './AlertCard.vue'

const props = defineProps({
    visible: Boolean,
    paymentDetails: {
        type: Object as () => {
            service?: string,
            professional?: string,
            date?: string,
            time?: string,
            amount?: number
        },
        required: false
    },
    paymentMethods: {
        type: Array as () => PaymentMethod[],
        required: false
    },
})
const emit = defineEmits(['close', 'pay'])
const selectedMethod = ref(props.paymentMethods?.[0]?.id || null)
const step = ref(1) // 1: confirm, 2: success, 3: error

const isMobile = computed(() => window.innerWidth < 768)

watch(() => props.visible, (val) => {
    if (val) {
        step.value = 1
        selectedMethod.value = props.paymentMethods?.[0]?.id || null
    }
})

function pay() {
    // Simulate payment process
    step.value = 1
    setTimeout(() => {
        // Randomly decide success or error for demo
        if (Math.random() > 0.2) {
            step.value = 2
        } else {
            step.value = 3
        }
    }, 1200)
}

function handleCloseAlert() {
    console.log('handleCloseAlert')
    emit('close')
}
</script>