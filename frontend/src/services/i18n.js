import { browser } from '$app/environment';
import { register, init, locale} from 'svelte-i18n';

// uses fetch to load locale files on demand
async function loadLocale(localeName) {
  const response = await fetch(`/lang/${localeName}.json`);
  if (!response.ok) {
    throw new Error(`Failed to load ${localeName} translation`);
  }
  return await response.json();
}

if (browser) {
  const cachedLocale = localStorage.getItem('cachedLocale') || 'en';
  register(cachedLocale, () => loadLocale(cachedLocale));
}

// initial locale is to english unless there is a cached locale
init({
  fallbackLocale: 'en',
  initialLocale: browser ? localStorage.getItem('cachedLocale') || 'en' : 'en',
});

export const AVAILABLE_LOCALES = ['en', 'lt'];

export function changeLocale(chosenLocale) {
  if (browser) {
    const cachedLocale = localStorage.getItem('cachedLocale');
    if (cachedLocale !== chosenLocale) {
      register(chosenLocale, () => loadLocale(chosenLocale));
      localStorage.setItem('cachedLocale', chosenLocale);
      locale.set(chosenLocale);
    }
  } else {
    locale.set(chosenLocale);
  }
}