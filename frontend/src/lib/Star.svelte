<script>
  import { createEventDispatcher } from 'svelte';

  export let filled = false;
  export let color = 'yellow';
  export let size = 24;
  export let clickable = false;
  export let outlined = false;

  const dispatch = createEventDispatcher();

  $: computedColor = (color === 'blue')
      ? "var(--information)"
      : (color === 'yellow')
          ? "var(--putty)"
          : (color === 'red')
              ? "var(--danger)"
              : color;
  $: cursorStyle = clickable ? "cursor: pointer;" : "";
  
  $: fillColor = outlined ? "none" : (filled ? computedColor : "none");
  $: strokeColor = outlined ? "black" : computedColor;

  function handleClick(event) {
    dispatch('click', { event });
  }
</script>

<!-- svelte-ignore a11y_no_static_element_interactions -->
<!-- svelte-ignore a11y_click_events_have_key_events -->
<svg
  xmlns="http://www.w3.org/2000/svg"
  width={size}
  height={size}
  viewBox="0 0 24 24"
  fill={fillColor}
  stroke={strokeColor}
  stroke-width="2"
  stroke-linecap="round"
  stroke-linejoin="round"
  style={cursorStyle}
  on:click={clickable ? handleClick : null}>
  <polygon points="12 2 15 8 22 9 17 14 18 21 12 18 6 21 7 14 2 9 9 8"/>
</svg>
