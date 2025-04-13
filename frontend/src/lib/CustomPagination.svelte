<script>
  import { Pagination, PaginationItem, PaginationLink } from '@sveltestrap/sveltestrap';
  import { createEventDispatcher, onMount } from 'svelte';

  export let pageCount;
  export let currentPage;

  const dispatch = createEventDispatcher();
  let startPage = 1;

  function updateSlice(newPage) {
    if (newPage < 1 || newPage > pageCount) return;

    if (pageCount > 3) {
      let newStart = newPage;
      if (newStart > pageCount - 2) {
        newStart = pageCount - 2;
      }
      if (newStart < 1) {
        newStart = 1;
      }
      startPage = newStart;
    } else {
      startPage = 1;
    }
    currentPage = newPage;
    dispatch('pageChange', { page: currentPage });
  }

  function handlePageChange(clickedPage) {
    updateSlice(clickedPage);
  }

  function handleFirst() {
    updateSlice(1);
  }

  function handleLast() {
    updateSlice(pageCount);
  }

  function handlePrevious() {
    updateSlice(currentPage - 1);
  }

  function handleNext() {
    updateSlice(currentPage + 1);
  }

  onMount(() => {
    if (currentPage) {
      if (pageCount > 3) {
        let newStart = currentPage;
        if (newStart > pageCount - 2) {
          newStart = pageCount - 2;
        }
        if (newStart < 1) {
          newStart = 1;
        }
        startPage = newStart;
      } else {
        startPage = 1;
      }
    } else {
      currentPage = 1;
      startPage = 1;
    }
  });
</script>

<Pagination size="md">
  {#if pageCount > 3}
    <PaginationItem>
      <PaginationLink first on:click={handleFirst} />
    </PaginationItem>
    <PaginationItem>
      <PaginationLink previous on:click={handlePrevious} />
    </PaginationItem>
  {/if}
  
  {#each Array(3) as _, i}
    {#if startPage + i <= pageCount}
      <PaginationItem active={currentPage === startPage + i}>
        <PaginationLink on:click={(event) => { event.preventDefault(); handlePageChange(startPage + i); }}>
          {startPage + i}
        </PaginationLink>
      </PaginationItem>
    {/if}
  {/each}
  
  {#if pageCount > 3}
    <PaginationItem>
      <PaginationLink next on:click={handleNext} />
    </PaginationItem>
    <PaginationItem>
      <PaginationLink last on:click={handleLast} />
    </PaginationItem>
  {/if}
</Pagination>
