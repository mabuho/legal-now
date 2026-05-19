<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ChevronUpDownIcon, XMarkIcon } from '@heroicons/vue/24/outline'
import { useFloating, autoUpdate } from '@floating-ui/vue'

interface Option {
  value: string
  label: string
  icon?: any
  iconClass?: string
}

const props = defineProps<{
  modelValue: string
  options: Option[]
  placeholder?: string
  clearable?: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
  'change': [option: Option | null]
}>()

// Component state
const isActive = ref(false)
const focusedOption = ref<Option | null>(null)
const buttonRef = ref<HTMLElement | null>(null) 
const dropdownRef = ref<HTMLElement | null>(null)
const selectWidth = ref(0)

const { floatingStyles, update } = useFloating(buttonRef, dropdownRef, {
  placement: 'bottom-start',
  whileElementsMounted: autoUpdate
})

// Computed
const selectedOptionData = computed(() => {
  return props.options.find(opt => opt.value === props.modelValue) || null
})

// Methods
const updateDropdownWidth = () => {
  const button = buttonRef.value
  if (button) {
    selectWidth.value = button.offsetWidth
    update?.()
  }
}

const toggleDropdown = () => {
  isActive.value = !isActive.value
  if (isActive.value) {
    updateDropdownWidth()
    focusedOption.value = selectedOptionData.value || props.options[0] || null
  }
}

const selectOption = (option: Option) => {
  emit('update:modelValue', option.value)
  emit('change', option)
  isActive.value = false
}

const navigateOptions = (direction: 'up' | 'down') => {
  if (!isActive.value) {
    isActive.value = true
    focusedOption.value = props.options[0]
    return
  }

  const currentIndex = focusedOption.value
    ? props.options.findIndex(opt => opt.value === focusedOption.value?.value)
    : -1

  let newIndex
  if (direction === 'up') {
    newIndex = currentIndex > 0 ? currentIndex - 1 : props.options.length - 1
  } else {
    newIndex = currentIndex < props.options.length - 1 ? currentIndex + 1 : 0
  }

  focusedOption.value = props.options[newIndex]
}

const clearSelection = (e: Event) => {
  e.stopPropagation() // Prevent dropdown from opening
  emit('update:modelValue', '')
  emit('change', null)
  isActive.value = false
}

// Close dropdown when clicking outside
const handleClickOutside = (event: Event) => {
  const select = buttonRef.value
  const dropdown = dropdownRef.value
  
  if (select && 
      !select.contains(event.target as Node) && 
      dropdown && 
      !dropdown.contains(event.target as Node)) {
    isActive.value = false
  }
}

// Lifecycle hooks
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  window.addEventListener('resize', updateDropdownWidth)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  window.removeEventListener('resize', updateDropdownWidth)
})
</script>

<template>
  <div 
    class="select-menu"
    :class="{ active: isActive }"
    @keydown.esc="isActive = false"
    @keydown.enter.prevent="isActive ? selectOption(focusedOption as Option) : toggleDropdown()"
    @keydown.space.prevent="isActive ? selectOption(focusedOption as Option) : toggleDropdown()"
    @keydown.up.prevent="navigateOptions('up')"
    @keydown.down.prevent="navigateOptions('down')"
    tabindex="0"
    role="combobox"
    aria-haspopup="listbox"
    :aria-expanded="isActive"
    aria-labelledby="select-label"
    aria-controls="select-options"
  >
    <div 
      ref="buttonRef"
      class="select-btn" 
      @click="toggleDropdown"
    >
      <div class="flex items-center">
        <component
          v-if="selectedOptionData?.icon"
          :is="selectedOptionData.icon"
          class="h-5 w-5 mr-3"
          :class="selectedOptionData.iconClass"
          aria-hidden="true"
        />
        <span class="sBtn-text">{{ selectedOptionData?.label || placeholder }}</span>
      </div>
      <div class="flex items-center">
        <button
          v-if="clearable && modelValue"
          type="button"
          class="p-1 hover:bg-gray-100 dark:hover:bg-gray-800 rounded-full mr-1"
          @click="clearSelection"
          aria-label="Limpiar selección"
        >
          <XMarkIcon class="h-4 w-4 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300" />
        </button>
        <ChevronUpDownIcon
          class="h-5 w-5 text-gray-500"
          aria-hidden="true"
        />
      </div>
    </div>
    <Teleport to="body">
      <ul
        v-if="isActive"
        ref="dropdownRef"
        id="select-options"
        class="options"
        role="listbox"
        :aria-activedescendant="focusedOption ? `option-${focusedOption.value}` : undefined"
        :style="{
          ...floatingStyles,
          width: selectWidth + 'px'
        }"
        :class="{
          'bg-white dark:bg-gray-900 border border-gray-300 dark:border-gray-700 rounded-xl shadow-lg z-[100]': isActive,
          'hidden': !isActive
        }"
      >
        <li
          v-for="option in options"
          :key="option.value"
          :id="'option-' + option.value"
          class="option"
          :class="{ 
            'bg-indigo-50 dark:bg-indigo-900/20': focusedOption?.value === option.value 
          }"
          @click="selectOption(option)"
          role="option"
          :aria-selected="modelValue === option.value"
        >
          <component
            v-if="option.icon"
            :is="option.icon"
            class="h-5 w-5"
            :class="option.iconClass"
            aria-hidden="true"
          />
          <span class="option-text">{{ option.label }}</span>
        </li>
      </ul>
    </Teleport>
  </div>
</template>

<style scoped>
.select-menu {
  @apply relative w-full;
}

.select-btn {
  @apply flex items-center justify-between w-full px-4 py-2 rounded-lg border bg-white dark:bg-gray-900 text-gray-900 dark:text-white cursor-pointer transition-all duration-200;
}

.select-menu.active .select-btn {
  @apply border-indigo-500 ring-2 ring-indigo-500;
}

.options {
  @apply overflow-y-auto max-h-60;
}

.options .option {
  @apply flex items-center px-4 py-2 cursor-pointer hover:bg-indigo-50 dark:hover:bg-indigo-900/20 transition-colors duration-200;
}

.option .option-text {
  @apply ml-3 text-gray-900 dark:text-white;
}

.sBtn-text {
  @apply text-gray-700 dark:text-gray-300;
}
</style> 