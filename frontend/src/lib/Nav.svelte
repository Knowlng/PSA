<script>
  import { onMount } from 'svelte';
  import {
    Collapse,
    NavbarToggler,
    NavbarBrand,
    Nav,
    Navbar,
    NavItem,
    NavLink,
    Dropdown,
    DropdownToggle,
    DropdownMenu,
    DropdownItem
  } from '@sveltestrap/sveltestrap';

  let isOpen = false;

  onMount(() => {
    if (window.innerWidth >= 768) {
      isOpen = true;
    }
  });

  function handleUpdate(event) {
    isOpen = event.detail.isOpen;
  }
</script>

<Navbar color="warning" light expand="md" container="md">
  <NavbarBrand href="/">Rotten Potatoes</NavbarBrand>
  <NavbarToggler on:click={() => (isOpen = !isOpen)} />
  <Collapse {isOpen} navbar expand="md" on:update={handleUpdate}>
    <Nav class="ms-auto" navbar>
      <Dropdown nav inNavbar>
        <DropdownToggle nav caret>Manage</DropdownToggle>
        <DropdownMenu end>
          <DropdownItem><NavLink href="/manageMovies">Movies</NavLink></DropdownItem>
          <DropdownItem><NavLink href="/manageGenres">Genres</NavLink></DropdownItem>
          <DropdownItem><NavLink href="/managePersonnel">Personnel</NavLink></DropdownItem>
        </DropdownMenu>
      </Dropdown>
    </Nav>
  </Collapse>
</Navbar>
