<script>
  import Nav from '$lib/Nav.svelte';
  import Footer from '$lib/Footer.svelte';
  import { Container } from '@sveltestrap/sveltestrap';
  import '/src/styles/variables.css';
  import Toasts from "$lib/ToastNotification/Toasts.svelte";
  import { onMount } from 'svelte';

  onMount(() => {
    let loggedIn = localStorage.getItem('userLoggedIn');
    if (loggedIn === null) {
      localStorage.setItem('userLoggedIn', false);
    }
  });

</script>

<div class="layout">
  <Toasts/>
  <header>
    <Nav />
  </header>
  <main>
    <div class="main-content">
      <Container class="p-0">
        <slot />
      </Container>
    </div>
  </main>
  <Footer />
</div>
  
<style>
  main {
    flex: 1;
    display: flex;
    background-color: var(--white);
  }

  .main-content {
    flex: 1;                 
    margin: 0 5rem;         
    background-color: var(--primary-color);
  }

  :global(html, body) {
    height: 100%;
    margin: 0;
  }
  
  .layout {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
  }

  
  @media (max-width: 768px) {
    .main-content {
      margin: 0;
    }
  }
</style>
  