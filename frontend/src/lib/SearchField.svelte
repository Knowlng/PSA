<script>
    import { Input } from '@sveltestrap/sveltestrap';
    import { createEventDispatcher } from 'svelte';
    
    export let placeholder = '';
    export let value = '';

    const dispatch = createEventDispatcher();
    
    function selectOnEnter(event) {
        if (event.key === "Enter") {
            event.preventDefault();
            dispatch('enter', { value });
            value = '';
        }
    }
</script>

<Input maxlength="50" type="text" bind:value={value} placeholder={placeholder} on:keydown={selectOnEnter}/>