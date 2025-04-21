<script>
  import { Input } from '@sveltestrap/sveltestrap';
  import { createEventDispatcher } from 'svelte';
  import { addToast } from "$lib/ToastNotification/toastStore.js";
  import { _, locale } from "svelte-i18n";

  export let placeholder = '';
  export let value = '';
  export let maxlength;
  export let feedback = '';
  export let invalid = false;
  export let searchEndpoint = '';
  export let clearOnSelect = false;
  export let language;

  let debounceTimer;
  const dispatch = createEventDispatcher();
  let suggestions = [];

  async function fetchSuggestions() {
    if (debounceTimer) clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
      if (!value.trim() || !searchEndpoint) {
        suggestions = [];
        return;
      }

      let endpoint;

      if(language) {
        endpoint = `${searchEndpoint}?query=${encodeURIComponent(value)}&locale=${encodeURIComponent(language)}`;
      } else {
        endpoint = `${searchEndpoint}?query=${encodeURIComponent(value)}&locale=${encodeURIComponent($locale)}`;
      }
      
      fetch(endpoint)
      .then(response => {
        return response.json();
      })
      .then(data => {
        suggestions = data;
      })
      .catch(error => {
        addToast({
          message: $_("ErrorMessages.somethingWentWrong"),
          type: "error",
        });
        suggestions = [];
      });
    }, 300);
  }

  function selectSuggestion(suggestion) {
    dispatch('select', { id: suggestion.id, name: suggestion.name });
    if (clearOnSelect) {
      value = '';
    }
    suggestions = [];
  }

  function onBlur() {
    setTimeout(() => {
      suggestions = [];
    }, 200);
  }

  function focus() {
    dispatch('fieldFocused');
  }

  function dispatchEnter(event) {
    if (event.key === 'Enter') {
      dispatch('enter');
    }
  }

</script>

<div class="search-container">
  <Input 
    maxlength={maxlength} 
    type="text" 
    bind:value 
    placeholder={placeholder} 
    feedback={feedback} 
    invalid={invalid}
    on:focus={focus}
    on:input={fetchSuggestions}
    on:blur={onBlur}
    on:keydown={dispatchEnter}
  />

  {#if suggestions.length > 0}
    <div class="suggestions">
      {#each suggestions.slice(0, 10) as suggestion}
        <button type="button" on:click={() => selectSuggestion(suggestion)}>
          {suggestion.name}
        </button>
      {/each}
    </div>
  {/if}
</div>

<style>
  .search-container {
    position: relative;
  }
  .suggestions {
    display: flex;
    flex-direction: column;
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    z-index: 999;
    list-style-type: none;
    margin: 0;
    padding: 0;
    border: 1px solid var(--silver);
    max-height: 200px;
    overflow-y: auto;
    background-color: var(--white);
  }
  button {
    all: unset;
    cursor: pointer;
    padding: 5px;
  }
  button:hover {
    background-color: var(--light-gray);
  }
</style>
