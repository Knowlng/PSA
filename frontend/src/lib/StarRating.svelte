<script>
    import Star from './Star.svelte';
    import { createEventDispatcher } from 'svelte';
  
    export let rating = 0;
    export let maxRating = 10;
    export let size = 32;
  
    const dispatch = createEventDispatcher();
    let hoveredRating = 0;

    function handleMouseOver(i) {
      hoveredRating = i;
    }

    function handleMouseOut() {
      hoveredRating = 0;
    }

    function handleClick(i) {
      rating = i;
      dispatch('rate', { rating });
    }
</script>

<div style="display: inline-flex;">
  <!-- svelte-ignore a11y_click_events_have_key_events -->
  <!-- svelte-ignore a11y_no_static_element_interactions -->
  <!-- svelte-ignore a11y_no_static_element_interactions -->
  {#each Array(maxRating) as _, index (index)}
    <!-- svelte-ignore a11y_click_events_have_key_events -->
    <!-- svelte-ignore a11y_mouse_events_have_key_events -->
    <div
      on:mouseover={() => handleMouseOver(index + 1)}
      on:mouseout={handleMouseOut}
      on:click={() => handleClick(index + 1)}
      style="display: inline-block;">
      <Star 
        filled={index + 1 <= (hoveredRating || rating)}
        color='blue'
        size={size}
        clickable={true} />
    </div>
  {/each}
</div>
  