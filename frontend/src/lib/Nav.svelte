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
      <NavItem>
        <NavLink href="/login">Login</NavLink>
      </NavItem>
      <NavItem>
        <NavLink href="/register">Register</NavLink>
      </NavItem>
      <Dropdown nav inNavbar>
        <DropdownToggle nav caret>Profile</DropdownToggle>
        <DropdownMenu end>
          <DropdownItem><NavLink href="/changeUsername">Change Username</NavLink></DropdownItem>
          <DropdownItem><NavLink href="/changePassword">Change Password</NavLink></DropdownItem>
        </DropdownMenu>
      </Dropdown>
      <Dropdown nav inNavbar>
        <DropdownToggle nav caret>Manage</DropdownToggle>
        <DropdownMenu end>
          <DropdownItem><NavLink href="/manageMovies">Manage Movies</NavLink></DropdownItem>
          <DropdownItem><NavLink href="/updateGenre">Update Genre</NavLink></DropdownItem>
          <DropdownItem><NavLink href="/createGenre">Create Genre</NavLink></DropdownItem>
          <DropdownItem><NavLink href="/updatePersonnel">Update Personnel</NavLink></DropdownItem>
          <DropdownItem><NavLink href="/createPersonnel">Create Personnel</NavLink></DropdownItem>
        </DropdownMenu>
      </Dropdown>
    </Nav>
  </Collapse>
</Navbar>
