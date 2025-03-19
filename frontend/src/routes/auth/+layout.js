import { browser } from '$app/environment';
import { redirect } from '@sveltejs/kit';

export function load() {
  if (browser && localStorage.getItem('userLoggedIn') === 'false') {
    throw redirect(302, '/');
  }
  return {};
}
