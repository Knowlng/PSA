<script>
    import { Input } from '@sveltestrap/sveltestrap';
    import { createEventDispatcher } from 'svelte';
    
    export let placeholder = '';
    export let value = '';
    export let maxlength;
    export let feedback = '';
    export let invalid = false;

    const dispatch = createEventDispatcher();
    
    function selectOnEnter(event) {
        if (event.key === "Enter") {
            event.preventDefault();
            dispatch('enter', { value });
            value = '';
        }
    }

    function focus() {
        dispatch('fieldFocused');
    }
</script>

<Input 
    maxlength={maxlength} 
    type="text" 
    bind:value={value} 
    placeholder={placeholder} 
    feedback={feedback} 
    invalid={invalid}
    on:keydown={selectOnEnter} 
    on:focus={focus}
/>