<script>
    import { _, locale } from "svelte-i18n";
    import { AVAILABLE_LOCALES, changeLocale } from "../services/i18n.js";
    import {
        Dropdown,
        DropdownToggle,
        DropdownMenu,
        DropdownItem
    } from '@sveltestrap/sveltestrap';

</script>

<Dropdown nav inNavbar>
    <DropdownToggle nav caret>{$_("LanguageDropdown.language")} {($locale || '').toUpperCase()}</DropdownToggle>
    <DropdownMenu end>
        {#each Object.values(AVAILABLE_LOCALES) as item}
            <DropdownItem class='uppercase' on:click={() => { 
                changeLocale(item); 
                location.reload(); 
            }}>
                {item}
            </DropdownItem>
        {/each}
    </DropdownMenu>
</Dropdown>

<style>
    :global(.uppercase) {
        text-transform: uppercase;
    }
</style>
  