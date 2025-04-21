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
  import LanguageDropdown from "$lib/LanguageDropdown.svelte";
  import { _ } from "svelte-i18n";

  let username;

  function handleLogout() {
    fetch(`/api/public/logout`, {
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
              message: $_("ErrorMessages.somethingWentWrong"),
              type: "error",
            });
            return null;
          });
        }
    })
    .then(result => {
      if (result !== null) {
        let currentLocale = localStorage.getItem('cachedLocale');
        localStorage.clear();
        localStorage.setItem('userLoggedIn', false);
        localStorage.setItem('cachedLocale', currentLocale);
        window.location.href = '/';
      }
    })
    .catch(error => {
      addToast({
        message: $_("ErrorMessages.somethingWentWrong"),
        type: "error",
      });
    });
  }

  let isOpen = false;

  onMount(() => {
    if (window.innerWidth >= 768) {
      isOpen = true;
    }
    username = localStorage.getItem('username');
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
        <NavLink href="/login">{$_("Nav.login")}</NavLink>
      </NavItem>
      <NavItem>
        <NavLink href="/register">{$_("Nav.register")}</NavLink>
      </NavItem>
      {/if}
      {#if localStorage.getItem('userLoggedIn') === 'true' && localStorage.getItem('role') === 'admin'}
        <Dropdown nav inNavbar>
          <DropdownToggle nav caret>{$_("Nav.manage")}</DropdownToggle>
          <DropdownMenu end>
            <DropdownItem><NavLink href="/manage/manageMovies">{$_("Nav.manageMovies")}</NavLink></DropdownItem>
            <DropdownItem><NavLink href="/manage/updateGenre">{$_("Nav.updateGenres")} </NavLink></DropdownItem>
            <DropdownItem><NavLink href="/manage/createGenre">{$_("Nav.createGenres")}</NavLink></DropdownItem>
            <DropdownItem><NavLink href="/manage/updatePersonnel">{$_("Nav.updatePersonnel")}</NavLink></DropdownItem>
            <DropdownItem><NavLink href="/manage/createPersonnel">{$_("Nav.createPersonnel")}</NavLink></DropdownItem>
            <DropdownItem><NavLink href="/manage/manageUsers">{$_("Nav.manageUsers")}</NavLink></DropdownItem>
          </DropdownMenu>
        </Dropdown>
      {/if}
      {#if localStorage.getItem('userLoggedIn') === 'true'}
        <Dropdown nav inNavbar>
          <DropdownToggle nav caret>{$_("Nav.profile")}</DropdownToggle>
          <DropdownMenu end>
            {#if username}
              <DropdownItem style="font-weight:bold; cursor:default;">{$_("Nav.loggedInAs")}{username}</DropdownItem>
            {/if}
            <DropdownItem><NavLink href="/auth/changeUsername">{$_("Nav.changeUsername")}</NavLink></DropdownItem>
            <DropdownItem><NavLink href="/auth/changePassword">{$_("Nav.changePasswordAndProfileSettings")}</NavLink></DropdownItem>
          </DropdownMenu>
        </Dropdown>
      {/if}
      <LanguageDropdown/>
      {#if localStorage.getItem('userLoggedIn') === 'true'}
        <NavItem>
          <NavLink on:click={handleLogout}>{$_("Nav.logout")}</NavLink>
        </NavItem>
      {/if}
    </Nav>
  </Collapse>
</Navbar>
