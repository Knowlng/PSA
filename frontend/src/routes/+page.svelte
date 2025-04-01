<script>
    import { Container, Form, FormGroup, Button, Input, Label, InputGroupText, InputGroup, ListGroup, ListGroupItem } from '@sveltestrap/sveltestrap'; 
    import CustomPagination from '$lib/CustomPagination.svelte';
    import SearchField from '$lib/SearchField.svelte';
    import {
        MAX_MOVIE_NAME_LENGTH,
        MAX_GENRE_SEARCH_LENGTH,
        MAX_PERSON_SEARCH_LENGTH,
        MAX_GROSS
    } from '$lib/consts.js';
    import { onMount } from 'svelte';
    import { addToast } from "$lib/ToastNotification/toastStore.js";
    import { filterStore } from '$lib/filterStore.js';
    import { get } from 'svelte/store';
    import { goto } from '$app/navigation'

    let movieName = '';
    let filterOpen = false;

    let ageRating = '';
    let fromDate = '';
    let toDate = '';
    let minGross = '';
    let maxGross = '';
    let genreName = '';
    let actorName = '';

    let selectedAgeRatings = [];
    let genreArray = [];
    let actorArray = [];
    let movies = [];

    let totalPages;
    let totalEntries;
    let perPage;
    let currentPage;

    function handleMovieEnter(event) {
        const { name, id } = event.detail;
        routeToMovie(id);
    }

    function routeToMovie(id) {
        filterStore.set({
            movieName,
            ageRating,
            fromDate,
            toDate,
            minGross,
            maxGross,
            genreName,
            actorName,
            selectedAgeRatings,
            genreArray,
            actorArray,
            perPage,
            totalPages,
            totalEntries,
            currentPage: currentPage ? currentPage : 1
        });
        goto(`/movie/${id}`);
    }

    function toggleFilter(event) {
        event.currentTarget.blur();
        filterOpen = !filterOpen;
        if(!filterOpen) {
            clearFields();
        }
    }

    function clearFields() {
        movieName = '';
        selectedAgeRatings = [];
        ageRating = '';
        fromDate = '';
        toDate = '';
        minGross = '';
        maxGross = '';
        actorArray = [];
        actorName = '';
        genreArray = [];
        genreName = '';
    }

    function onAgeRatingSelect() {
        if (!selectedAgeRatings.includes(ageRating) && ageRating !== '') {
            selectedAgeRatings = [...selectedAgeRatings, ageRating];
        }
        ageRating = '';
    }

    function removeAgeRating(rating) {
        selectedAgeRatings = selectedAgeRatings.filter(r => r !== rating);
    }

    function handleGenreEnter(event) {
        const { id, name } = event.detail;
        if (genreArray.some(item => item.id === id)) {
            return;
        }
        genreArray = [...genreArray, { id, name }];
    }

    function removeGenre(index) {
        genreArray = genreArray.filter((_, i) => i !== index);
    }

    function handlePersonEnter(event) {
        const { id, name } = event.detail;
        if (actorArray.some(item => item.id === id)) {
            return;
        }
        actorArray = [...actorArray, { actorId: id, name: name, role: "any role" }];
    }
    
    function removeActor(index) {
        actorArray = actorArray.filter((_, i) => i !== index);
    }

    function search() {
        fetchMovies(currentPage, perPage);
    }

    function handlePageChange(event) {
        const { page } = event.detail;
        currentPage = page;
        fetchMovies(page, Number(perPage));

    }

    async function fetchMovies(page, perPage) {
        let genreIdsArray = [];
        if (genreArray.length > 0) {
            genreIdsArray = genreArray.map(genre => genre.id);
        }
        const filterPayload = {
            movieName: movieName,
            selectedAgeRatings: selectedAgeRatings,
            fromDate: fromDate,
            toDate: toDate,
            minGross: minGross,
            maxGross: maxGross,
            actorFilters: actorArray,
            genreIds: genreIdsArray,
            page: page,
            size: perPage
        };

        try {
            const response = await fetch('/api/public/films/filter', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(filterPayload),
                credentials: 'include'
            });

            if (!response.ok) {
                addToast({
                    message: "Something went wrong. Please try again later.",
                    type: "error",
                });
                return;
            }

            const data = await response.json();
            movies = data.content;
            totalPages = data.totalPages;
            totalEntries = data.totalElements;
        } catch (error) {
            addToast({
                message: "Something went wrong. Please try again later.",
                type: "error",
            });
        }
    }
    
    function handlePerPageChange(event) {
        perPage = event.target.value;
        fetchMovies(currentPage, Number(perPage));
    }


    onMount(() => {
        const storedFilters = get(filterStore);
        if (storedFilters) {
            movieName = storedFilters.movieName || '';
            ageRating = storedFilters.ageRating || '';
            fromDate = storedFilters.fromDate || '';
            toDate = storedFilters.toDate || '';
            minGross = storedFilters.minGross || '';
            maxGross = storedFilters.maxGross || '';
            genreName = storedFilters.genreName || '';
            actorName = storedFilters.actorName || '';
            selectedAgeRatings = storedFilters.selectedAgeRatings || [];
            genreArray = storedFilters.genreArray || [];
            actorArray = storedFilters.actorArray || [];
            perPage = storedFilters.perPage ? storedFilters.perPage.toString() : '10';
            totalPages = storedFilters.totalPages;
            totalEntries = storedFilters.totalEntries;
            currentPage = storedFilters.currentPage || 1;
        }
        fetchMovies(currentPage, Number(perPage));
    });
    
</script>

<Container class="mt-5 p-0">
    <h1 class="text-center mb-5">Search Movies</h1>
    <Form class='mb-5'>
        <Container class="w-75 mx-auto" style="min-width: 450px; max-width: 700px;">
            <FormGroup class="mb-4">
                <Container class="mb-3">
                    <SearchField
                        placeholder="Enter movie name" 
                        maxlength={MAX_MOVIE_NAME_LENGTH} 
                        bind:value={movieName}
                        on:select={handleMovieEnter}
                        searchEndpoint={`/api/public/search-film`}
                    />
                </Container>
                {#if filterOpen}
                    <Container>
                        <FormGroup floating label="Select multiple age ratings" class="override-mb">
                            <Input type="select" bind:value={ageRating} on:change={onAgeRatingSelect}>
                            <option value="">Select</option>
                            <option value="G">G</option>
                            <option value="PG">PG</option>
                            <option value="PG-13">PG-13</option>
                            <option value="R">R</option>
                            <option value="NC-17">NC-17</option>
                            </Input>
                        </FormGroup>
                    </Container>
                    <Container
                        class={`${selectedAgeRatings.length > 0 ? "mb-3" : ""} d-flex gap-2 justify-content-center`}
                    >
                        {#each selectedAgeRatings as rating (rating)}
                          <span>
                            {rating}
                            <Button class="mr-3" color="danger" on:click={() => removeAgeRating(rating)}>X</Button>
                          </span>
                        {/each}
                    </Container>
                    <Container class="mb-3 d-flex gap-2 justify-content-center align-items-center">
                        <p class="mb-0">From</p>
                        <Input type="date" bind:value={fromDate} />
                        <p class="mb-0">To</p>
                        <Input type="date" bind:value={toDate} />
                    </Container>
                    <Container class="mb-3 d-flex gap-2 justify-content-center align-items-center">
                        <p class="mb-0">From</p>
                        <Input 
                            placeholder="Gross"
                            type="number" step="1" min="0"
                            max={MAX_GROSS}
                            bind:value={minGross}
                        />
                        <InputGroupText>$</InputGroupText>
                        <p class="mb-0">To</p>
                        <Input 
                            placeholder="Gross"
                            type="number" step="1" min="0"
                            max={MAX_GROSS}
                            bind:value={maxGross}
                        />
                        <InputGroupText>$</InputGroupText>
                    </Container>
                    <SearchField placeholder="Enter genre" maxlength={MAX_GENRE_SEARCH_LENGTH} bind:value={genreName} on:select={handleGenreEnter} searchEndpoint={`/api/public/search-genre`} clearOnSelect={true}/>
                    <ListGroup flush class="mt-3">
                        {#each genreArray as { id, name }, index}
                        <ListGroupItem tag="a" class="d-flex justify-content-between align-items-center pl-1">
                          <Container class="p-0 w-25">
                            {name}
                          </Container>
                          <Container class="d-flex justify-content-end p-0">
                            <Button type="button" color="danger" on:click={() => removeGenre(index)}>Remove</Button>  
                          </Container>
                        </ListGroupItem>
                      {/each}
                    </ListGroup>
                    <SearchField placeholder="Enter Person Name" maxlength={MAX_PERSON_SEARCH_LENGTH} bind:value={actorName} on:select={handlePersonEnter} searchEndpoint={`/api/public/search-person`} clearOnSelect={true}/>
                    <ListGroup flush class="mt-3">
                    {#each actorArray as { id, name}, index}
                        <ListGroupItem tag="a" class="d-flex justify-content-between align-items-center pl-1">
                        <Container class="p-0 w-25">
                            {name}
                        </Container>
                        <Container class="d-flex justify-content-end p-0">
                            <Input type="select" class="w-50" bind:value={actorArray[index].role}>
                            {#each ["any role", "director", "writer", "actor"] as option}
                                <option value={option}>{option}</option>
                            {/each}
                            </Input>
                            <Button type="button" color="danger" on:click={() => removeActor(index)}>Remove</Button>  
                        </Container>
                        </ListGroupItem>
                    {/each}
                    </ListGroup>
                {/if}
                <Container class="d-flex justify-content-center gap-3">
                    <Button type="button" on:click={toggleFilter} class="ml-2" color="primary">{filterOpen ? 'Close Filter' : 'Filter'}</Button>
                    <Button class="text-center" color="success" on:click={search}>Search</Button>
                </Container>
            </FormGroup>
        </Container>
    </Form>
    <Container>
        {#if totalPages > 1}
            <Container class="d-flex justify-content-center mb-1">
                <CustomPagination on:pageChange={handlePageChange} pageCount={totalPages} currentPage={currentPage}/>
            </Container>
        {/if}
    </Container>
    {#if movies && movies.length > 0}
        <Container class="d-flex justify-content-end align-items-center mb-3" style="min-width: 450px; max-width: 700px;">
            <Container>Total pages: {totalPages}</Container>
            <Container class="text-center">Total Entries: {totalEntries}</Container>
            <Container style="min-width: 100px; max-width: 100px;" class="text-end p-0 d-flex align-items-center justify-content-end">
                <Label class="m-0" for="perPage">Per page:</Label>
            </Container>
            <Container style="min-width: 100px; max-width: 100px;">
                <select key={perPage} type="select" bind:value={perPage} on:change={handlePerPageChange}>
                    <option value="10">10</option>
                    <option value="25">25</option>
                    <option value="50">50</option>
                    <option value="75">75</option>
                    <option value="100">100</option>
                </select>
            </Container>
        </Container>
    {:else}
        <Container class="text-center mt-4">
            <h4>No movies found</h4>
            <h4>Try altering your search parameters</h4>
        </Container>
    {/if}
    <Container class="p-0 movie-entry mb-4" style="min-width: 450px; max-width: 700px;">
        {#each movies as movie, index (movie.id)}
          <!-- svelte-ignore a11y_click_events_have_key_events -->
          <!-- svelte-ignore a11y_no_static_element_interactions -->
          <div style="border: 1px solid black;" class="{index % 2 === 0 ? 'first-panel' : 'second-panel'} m-0 pb-3 pt-3 d-flex " on:click={() => routeToMovie(movie.id)}>
            <Container>
                <h4><strong>Title:</strong> {movie.filmName || '-'}</h4>
                <p><strong>Release Date:</strong> {movie.filmReleaseDate || '-'}</p>
                <p><strong>Rating:</strong> {movie.filmRating || '-'}</p>
                <p><strong>Gross $:</strong> {movie.filmGross || '-'}</p>
                {#if movie.genres && movie.genres.length > 0}
                <p>
                  <strong>Genres:</strong>
                  {#each movie.genres as genre, i}
                    {genre.name || '-'}{i < movie.genres.length - 1 ? ', ' : ''}
                  {/each}
                </p>
              {/if}
              {#if movie.actors && movie.actors.length > 0}
                <p>
                  <strong>Personnel:</strong>
                  {#each movie.actors.slice().sort((a, b) => {
                    const priority = { director: 1, writer: 2, actor: 3 };
                    return (priority[a.role] || 4) - (priority[b.role] || 4);
                  }) as actor, i}
                    {actor.name || '-'} ({actor.role || '-'}){i < movie.actors.length - 1 ? ', ' : ''}
                  {/each}
                </p>
              {/if}              
            </Container>
            <Container class="p-0">
                <h5 class="pt-1">Brief description</h5>
                <p>{movie.filmDesc?.length > 100 ? movie.filmDesc.slice(0, 100) + ' <..>' : movie.filmDesc || '-'}</p>
            </Container>
        </div>
        {/each}
    </Container>
</Container>

<style>
    :global(input[type="number"]::-webkit-outer-spin-button),
    :global(input[type="number"]::-webkit-inner-spin-button) {
        -webkit-appearance: none;
        margin: 0;
    }
    
    :global(.list-group-item:last-child) {
        margin-bottom: 2rem;
    }

    :global(.first-panel) {
        background-color: var(--marzipan);
    }

    :global(.first-panel:hover) {
        cursor: pointer;
        background-color: var(--amber);
    }

    :global(.second-panel) {
        background-color: var(--putty);
    }

    :global(.second-panel:hover) {
        cursor: pointer;
        background-color: var(--amber);
    }

    select {
        display: block;
        width: 100%;
        font-size: 1rem;
        line-height: 1.5;
        background-size: 8px 10px;
        border-radius: 0.25rem;
    }
</style>
