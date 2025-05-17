<template>
  <form @submit.prevent="handleSubmit" class="space-y-8">
    <div class="space-y-6">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="relative">
          <input
            v-model="formData.firstName"
            type="text"
            id="firstName"
            class="peer h-12 w-full border-b-2 border-gray-300 text-gray-900 placeholder-transparent focus:outline-none focus:border-blue-600 bg-transparent"
            :class="{ 'border-red-500': errors.firstName }"
            placeholder="Nombre"
          />
          <label
            for="firstName"
            class="absolute left-0 -top-3.5 text-gray-600 text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3 peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm"
          >
            Nombre
          </label>
          <p v-if="errors.firstName" class="mt-1 text-sm text-red-500">{{ errors.firstName }}</p>
        </div>

        <div class="relative">
          <input
            v-model="formData.lastName"
            type="text"
            id="lastName"
            class="peer h-12 w-full border-b-2 border-gray-300 text-gray-900 placeholder-transparent focus:outline-none focus:border-blue-600 bg-transparent"
            :class="{ 'border-red-500': errors.lastName }"
            placeholder="Apellido"
          />
          <label
            for="lastName"
            class="absolute left-0 -top-3.5 text-gray-600 text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3 peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm"
          >
            Apellido
          </label>
          <p v-if="errors.lastName" class="mt-1 text-sm text-red-500">{{ errors.lastName }}</p>
        </div>
      </div>

      <div class="relative">
        <input
          v-model="formData.email"
          type="email"
          id="email"
          class="peer h-12 w-full border-b-2 border-gray-300 text-gray-900 placeholder-transparent focus:outline-none focus:border-blue-600 bg-transparent"
          :class="{ 'border-red-500': errors.email }"
          placeholder="Correo Electrónico"
        />
        <label
          for="email"
          class="absolute left-0 -top-3.5 text-gray-600 text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3 peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm"
        >
          Correo Electrónico
        </label>
        <p v-if="errors.email" class="mt-1 text-sm text-red-500">{{ errors.email }}</p>
      </div>

      <div class="relative">
        <input
          v-model="formData.phone"
          type="tel"
          id="phone"
          class="peer h-12 w-full border-b-2 border-gray-300 text-gray-900 placeholder-transparent focus:outline-none focus:border-blue-600 bg-transparent"
          :class="{ 'border-red-500': errors.phone }"
          placeholder="Teléfono"
        />
        <label
          for="phone"
          class="absolute left-0 -top-3.5 text-gray-600 text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3 peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm"
        >
          Teléfono
        </label>
        <p v-if="errors.phone" class="mt-1 text-sm text-red-500">{{ errors.phone }}</p>
      </div>

      <div class="relative">
        <input
          v-model="formData.password"
          type="password"
          id="password"
          class="peer h-12 w-full border-b-2 border-gray-300 text-gray-900 placeholder-transparent focus:outline-none focus:border-blue-600 bg-transparent"
          :class="{ 'border-red-500': errors.password }"
          placeholder="Contraseña"
        />
        <label
          for="password"
          class="absolute left-0 -top-3.5 text-gray-600 text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3 peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm"
        >
          Contraseña
        </label>
        <p v-if="errors.password" class="mt-1 text-sm text-red-500">{{ errors.password }}</p>
      </div>

      <div class="relative">
        <input
          v-model="formData.confirmPassword"
          type="password"
          id="confirmPassword"
          class="peer h-12 w-full border-b-2 border-gray-300 text-gray-900 placeholder-transparent focus:outline-none focus:border-blue-600 bg-transparent"
          :class="{ 'border-red-500': errors.confirmPassword }"
          placeholder="Confirmar Contraseña"
        />
        <label
          for="confirmPassword"
          class="absolute left-0 -top-3.5 text-gray-600 text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3 peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm"
        >
          Confirmar Contraseña
        </label>
        <p v-if="errors.confirmPassword" class="mt-1 text-sm text-red-500">
          {{ errors.confirmPassword }}
        </p>
      </div>

      <div class="flex items-center space-x-3">
        <div class="relative flex items-start">
          <div class="flex items-center h-5">
            <input
              type="checkbox"
              v-model="formData.terms"
              id="terms"
              class="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-600 transition-colors cursor-pointer"
            />
          </div>
          <div class="ml-3 text-sm">
            <label for="terms" class="font-medium text-gray-700 cursor-pointer">
              Acepto los términos y condiciones
            </label>
          </div>
        </div>
      </div>
      <p v-if="errors.terms" class="mt-1 text-sm text-red-500">{{ errors.terms }}</p>
    </div>

    <button
      type="submit"
      class="w-full flex justify-center py-3 px-4 border border-transparent rounded-xl shadow-sm text-lg font-medium text-white bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transform transition-all duration-200 hover:-translate-y-0.5"
    >
      Registrarse
    </button>
  </form>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const formData = reactive({
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  terms: false,
})

const errors = reactive({
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  terms: '',
})

const validateForm = () => {
  let isValid = true

  // Reset errors
  Object.keys(errors).forEach((key) => (errors[key] = ''))

  if (!formData.firstName) {
    errors.firstName = 'El nombre es requerido'
    isValid = false
  }

  if (!formData.lastName) {
    errors.lastName = 'El apellido es requerido'
    isValid = false
  }

  if (!formData.email) {
    errors.email = 'El correo electrónico es requerido'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
    errors.email = 'Ingrese un correo electrónico válido'
    isValid = false
  }

  if (!formData.phone) {
    errors.phone = 'El teléfono es requerido'
    isValid = false
  }

  if (!formData.password) {
    errors.password = 'La contraseña es requerida'
    isValid = false
  } else if (formData.password.length < 8) {
    errors.password = 'La contraseña debe tener al menos 8 caracteres'
    isValid = false
  }

  if (!formData.confirmPassword) {
    errors.confirmPassword = 'Confirme su contraseña'
    isValid = false
  } else if (formData.password !== formData.confirmPassword) {
    errors.confirmPassword = 'Las contraseñas no coinciden'
    isValid = false
  }

  if (!formData.terms) {
    errors.terms = 'Debe aceptar los términos y condiciones'
    isValid = false
  }

  return isValid
}

const handleSubmit = async () => {
  if (!validateForm()) return

  try {
    // Here you would typically make an API call to register the user
    console.log('Form submitted:', formData)
    // On successful registration, redirect to login
    router.push('/login')
  } catch (error) {
    console.error('Registration error:', error)
  }
}
</script>

<style scoped>
.input {
  display: block;
  width: 100%;
  margin-bottom: 12px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
}
.btn-green {
  background-color: #1e824c;
  color: white;
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
</style>
