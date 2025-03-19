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
  import { addToast } from "$lib/ToastNotification/toastStore.js";

  function handleLogout() {
    fetch(`/api/logout`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
    .then(response => {
        if (!response.ok) {
          return response.text().then(text => {
            addToast({
              message: "Something went wrong. Please try again later.",
              type: "error",
            });
            return null;
          });
        }
    })
    .then(result => {
      if (result !== null) {
        localStorage.setItem('userLoggedIn', false);
        window.location.href = '/';
      }
    })
    .catch(error => {
      addToast({
        message: "Something went wrong. Please try again later.",
        type: "error",
      });
    });
  }

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
      {#if localStorage.getItem('userLoggedIn') === 'false'}
      <NavItem>
        <NavLink href="/login">Login</NavLink>
      </NavItem>
      <NavItem>
        <NavLink href="/register">Register</NavLink>
      </NavItem>
      {/if}
      {#if localStorage.getItem('userLoggedIn') === 'true'}
        <NavItem>
          <NavLink on:click={handleLogout}>Logout</NavLink>
        </NavItem>
      {/if}
      {#if localStorage.getItem('userLoggedIn') === 'true'}
        <Dropdown nav inNavbar>
          <DropdownToggle nav caret>Profile</DropdownToggle>
          <DropdownMenu end>
            <DropdownItem><NavLink href="/auth/changeUsername">Change Username</NavLink></DropdownItem>
            <DropdownItem><NavLink href="/auth/changePassword">Change Password</NavLink></DropdownItem>
          </DropdownMenu>
        </Dropdown>
      {/if}
      {#if localStorage.getItem('userLoggedIn') === 'true'}
        <Dropdown nav inNavbar>
          <DropdownToggle nav caret>Manage</DropdownToggle>
          <DropdownMenu end>
            <DropdownItem><NavLink href="/auth/manageMovies">Manage Movies</NavLink></DropdownItem>
            <DropdownItem><NavLink href="/auth/updateGenre">Update Genre</NavLink></DropdownItem>
            <DropdownItem><NavLink href="/auth/createGenre">Create Genre</NavLink></DropdownItem>
            <DropdownItem><NavLink href="/auth/updatePersonnel">Update Personnel</NavLink></DropdownItem>
            <DropdownItem><NavLink href="/auth/createPersonnel">Create Personnel</NavLink></DropdownItem>
          </DropdownMenu>
        </Dropdown>
      {/if}
    </Nav>
  </Collapse>
</Navbar>
