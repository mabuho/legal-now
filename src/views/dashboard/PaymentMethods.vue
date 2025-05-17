<script setup lang="ts">
import { ref } from 'vue'
import {
  CreditCardIcon,
  PlusIcon,
  TrashIcon,
  CheckCircleIcon
} from '@heroicons/vue/24/outline'

interface PaymentMethod {
  id: string
  type: 'credit' | 'debit'
  cardNumber: string
  expiryDate: string
  cardHolder: string
  isDefault: boolean
}

const paymentMethods = ref<PaymentMethod[]>([
  {
    id: '1',
    type: 'credit',
    cardNumber: '•••• •••• •••• 4242',
    expiryDate: '12/25',
    cardHolder: 'Juan Pérez',
    isDefault: true
  },
  {
    id: '2',
    type: 'debit',
    cardNumber: '•••• •••• •••• 5555',
    expiryDate: '09/24',
    cardHolder: 'Juan Pérez',
    isDefault: false
  }
])

const showAddForm = ref(false)
const newCard = ref({
  type: 'credit',
  cardNumber: '',
  expiryDate: '',
  cardHolder: '',
  cvv: ''
})

const addPaymentMethod = () => {
  // Here you would typically integrate with a payment gateway
  // For demo purposes, we'll just add the card to the list
  paymentMethods.value.push({
    id: Date.now().toString(),
    type: newCard.value.type as 'credit' | 'debit',
    cardNumber: '•••• •••• •••• ' + newCard.value.cardNumber.slice(-4),
    expiryDate: newCard.value.expiryDate,
    cardHolder: newCard.value.cardHolder,
    isDefault: false
  })
  
  showAddForm.value = false
  newCard.value = {
    type: 'credit',
    cardNumber: '',
    expiryDate: '',
    cardHolder: '',
    cvv: ''
  }
}

const setDefaultPaymentMethod = (id: string) => {
  paymentMethods.value = paymentMethods.value.map(method => ({
    ...method,
    isDefault: method.id === id
  }))
}

const removePaymentMethod = (id: string) => {
  paymentMethods.value = paymentMethods.value.filter(method => method.id !== id)
}
</script>

<template>
  <div class="space-y-6">
    <div class="sm:flex sm:items-center sm:justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
          Métodos de Pago
        </h1>
        <p class="mt-2 text-sm text-gray-700 dark:text-gray-300">
          Administra tus tarjetas y métodos de pago
        </p>
      </div>
      <button
        v-if="!showAddForm"
        @click="showAddForm = true"
        class="mt-4 sm:mt-0 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
      >
        <PlusIcon class="h-5 w-5 mr-2" />
        Agregar Método de Pago
      </button>
    </div>

    <!-- Add Payment Method Form -->
    <div v-if="showAddForm" class="bg-white dark:bg-gray-800 shadow sm:rounded-lg">
      <div class="px-4 py-5 sm:p-6">
        <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-4">
          Agregar Nueva Tarjeta
        </h3>
        <div class="grid grid-cols-1 gap-6 sm:grid-cols-2">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
              Tipo de Tarjeta
            </label>
            <select
              v-model="newCard.type"
              class="mt-1 block w-full rounded-md border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:ring-indigo-500 focus:border-indigo-500"
            >
              <option value="credit">Crédito</option>
              <option value="debit">Débito</option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
              Número de Tarjeta
            </label>
            <input
              v-model="newCard.cardNumber"
              type="text"
              maxlength="16"
              placeholder="1234 5678 9012 3456"
              class="mt-1 block w-full rounded-md border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:ring-indigo-500 focus:border-indigo-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
              Fecha de Expiración
            </label>
            <input
              v-model="newCard.expiryDate"
              type="text"
              placeholder="MM/YY"
              maxlength="5"
              class="mt-1 block w-full rounded-md border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:ring-indigo-500 focus:border-indigo-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
              CVV
            </label>
            <input
              v-model="newCard.cvv"
              type="password"
              maxlength="4"
              placeholder="123"
              class="mt-1 block w-full rounded-md border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:ring-indigo-500 focus:border-indigo-500"
            />
          </div>

          <div class="sm:col-span-2">
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
              Nombre del Titular
            </label>
            <input
              v-model="newCard.cardHolder"
              type="text"
              placeholder="NOMBRE COMO APARECE EN LA TARJETA"
              class="mt-1 block w-full rounded-md border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:ring-indigo-500 focus:border-indigo-500"
            />
          </div>
        </div>

        <div class="mt-6 flex justify-end space-x-4">
          <button
            @click="showAddForm = false"
            class="inline-flex items-center px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm text-sm font-medium text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Cancelar
          </button>
          <button
            @click="addPaymentMethod"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Agregar Tarjeta
          </button>
        </div>
      </div>
    </div>

    <!-- Payment Methods List -->
    <div class="bg-white dark:bg-gray-800 shadow sm:rounded-lg">
      <div class="px-4 py-5 sm:p-6">
        <div class="space-y-4">
          <div
            v-for="method in paymentMethods"
            :key="method.id"
            class="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg"
          >
            <div class="flex items-center space-x-4">
              <CreditCardIcon class="h-8 w-8 text-gray-400" />
              <div>
                <div class="flex items-center space-x-2">
                  <p class="font-medium text-gray-900 dark:text-white">
                    {{ method.cardNumber }}
                  </p>
                  <span
                    v-if="method.isDefault"
                    class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400"
                  >
                    <CheckCircleIcon class="h-4 w-4 mr-1" />
                    Predeterminada
                  </span>
                </div>
                <p class="text-sm text-gray-500 dark:text-gray-400">
                  Vence: {{ method.expiryDate }} | {{ method.cardHolder }}
                </p>
              </div>
            </div>
            <div class="flex items-center space-x-2">
              <button
                v-if="!method.isDefault"
                @click="setDefaultPaymentMethod(method.id)"
                class="text-sm text-indigo-600 dark:text-indigo-400 hover:text-indigo-500"
              >
                Establecer como predeterminada
              </button>
              <button
                v-if="!method.isDefault"
                @click="removePaymentMethod(method.id)"
                class="p-2 text-gray-400 hover:text-red-500"
              >
                <TrashIcon class="h-5 w-5" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template> 