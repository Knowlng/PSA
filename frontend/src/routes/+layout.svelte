<script>
  import Nav from '$lib/Nav.svelte';
  import { Container } from '@sveltestrap/sveltestrap';
  import '/src/styles/variables.css';
</script>

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

<footer>
</footer>

<style>
    main {
        background-color: var(--white);
    }
    .main-content {
        margin: 0 5rem 0 5rem;
        background-color: var(--primary-color);
    }

    @media (max-width: 768px) {
        .main-content {
            margin: 0;
        }
    }
</style>
