import { browser } from '$app/environment';
import { redirect } from '@sveltejs/kit';

export function load() {
  if (browser && (localStorage.getItem('userLoggedIn') === 'false' || localStorage.getItem('role') !== 'admin')) {
    throw redirect(302, '/');
  }
  return {};
}
