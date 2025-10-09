<template>
  <teleport to="body">
    <div class="fixed top-4 left-1/2 -translate-x-1/2 z-[9999] flex flex-col gap-2 w-full max-w-md px-4">
      <transition-group name="toast">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          class="rounded-lg shadow-lg backdrop-blur-sm p-4 flex items-center gap-3 animate-slide-down"
          :class="getToastClass(toast.type)"
          @click="removeToast(toast.id)"
        >
          <div class="flex-shrink-0">
            <component :is="getIcon(toast.type)" :size="20" />
          </div>
          <p class="flex-1 font-sans text-sm font-medium">{{ toast.message }}</p>
          <button
            class="flex-shrink-0 opacity-70 hover:opacity-100 transition-opacity"
            @click.stop="removeToast(toast.id)"
          >
            <XIcon :size="16" />
          </button>
        </div>
      </transition-group>
    </div>
  </teleport>
</template>

<script setup lang="ts">
import { CheckCircle2, XCircle, AlertTriangle, Info, X as XIcon } from 'lucide-vue-next'
import { useToast, type ToastType } from '@/composables/useToast'

const { toasts, removeToast } = useToast()

const getToastClass = (type: ToastType): string => {
  const baseClass = 'border-l-4 cursor-pointer'
  
  switch (type) {
    case 'success':
      return `${baseClass} bg-green-500/20 border-green-500 text-green-100`
    case 'error':
      return `${baseClass} bg-red-500/20 border-red-500 text-red-100`
    case 'warning':
      return `${baseClass} bg-yellow-500/20 border-yellow-500 text-yellow-100`
    case 'info':
      return `${baseClass} bg-blue-500/20 border-blue-500 text-blue-100`
    default:
      return `${baseClass} bg-gray-500/20 border-gray-500 text-gray-100`
  }
}

const getIcon = (type: ToastType) => {
  switch (type) {
    case 'success':
      return CheckCircle2
    case 'error':
      return XCircle
    case 'warning':
      return AlertTriangle
    case 'info':
      return Info
    default:
      return Info
  }
}
</script>

<style scoped>
@keyframes slide-down {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-slide-down {
  animation: slide-down 0.3s ease-out;
}

.toast-enter-active {
  transition: all 0.3s ease-out;
}

.toast-leave-active {
  transition: all 0.2s ease-in;
}

.toast-enter-from {
  opacity: 0;
  transform: translateY(-20px);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(100px);
}

.toast-move {
  transition: transform 0.3s ease;
}
</style>

