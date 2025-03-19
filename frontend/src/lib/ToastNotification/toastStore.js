import { writable } from "svelte/store";

export const toasts = writable([]);

export const addToast = (toast) => {

  const id = Math.floor(Math.random() * 10000);

  const defaults = {
    id,
    type: "info",
    dismissible: true,
    timeout: 3000,
  };

  const toastWithDefaults = { ...defaults, ...toast };

  toasts.update((all) => [toastWithDefaults, ...all]);
  
  if (toastWithDefaults.timeout) {
    setTimeout(() => dismissToast(id), toastWithDefaults.timeout);
  }
};

export const dismissToast = (id) => {
  toasts.update((all) => all.filter((t) => t.id !== id));
};