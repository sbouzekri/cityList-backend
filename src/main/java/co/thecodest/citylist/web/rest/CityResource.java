package co.thecodest.citylist.web.rest;

import co.thecodest.citylist.domain.City;
import co.thecodest.citylist.repository.CityRepository;
import co.thecodest.citylist.service.impl.CityServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.websocket.server.PathParam;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
@Transactional
public class CityResource {

    private final Logger log = LoggerFactory.getLogger(CityResource.class);
    private final CityRepository cityRepository;
    private final CityServiceImpl cityService;


    public CityResource(CityServiceImpl cityService, CityRepository cityRepository) {
        this.cityService = cityService;
        this.cityRepository = cityRepository;
    }

    /**
     * {@code POST  /cities} : Create a new city.
     *
     * @param city the city to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new city, or with status {@code 400 (Bad Request)} if the city has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cities")
    public ResponseEntity<City> createCity(@RequestBody City city) throws URISyntaxException {
        log.debug("REST request to save City : {}", city);
        if (city.getId() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        City result = cityService.save(city);
        return ResponseEntity
                .created(new URI("/api/cities/" + result.getId()))
                .body(result);
    }

    /**
     * {@code PUT  /cities/:id} : Updates an existing city.
     *
     * @param id   the id of the city to save.
     * @param city the city to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated city,
     * or with status {@code 400 (Bad Request)} if the city is not valid,
     * or with status {@code 500 (Internal Server Error)} if the city couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cities/{id}")
    public ResponseEntity<City> updateCity(@PathVariable(value = "id", required = false) final Long id, @RequestBody City city)
            throws URISyntaxException {
        log.debug("REST request to update City : {}, {}", id, city);
        if (city.getId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!Objects.equals(id, city.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!cityRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        City result = cityService.save(city);
        return ResponseEntity
                .ok()
                .body(result);
    }

    /**
     * {@code GET  } : get all the cities.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cities in body.
     */
    @GetMapping("/cities")
    public Page<City> getAllCities(@PathParam("name") String name, @PathParam("page") int page, @PathParam("size") int size) {
        log.debug("REST request to get a page of Cities");
        return cityService.findAllByName(name, PageRequest.of(page, size));
    }

    /**
     * {@code GET  /cities/:id} : get the "id" city.
     *
     * @param id the id of the city to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the city, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cities/{id}")
    public ResponseEntity getCity(@PathVariable Long id) {
        log.debug("REST request to get City : {}", id);
        Optional<City> city = cityService.findOne(id);
        return city.map((response) -> (ResponseEntity.ok()).body(response))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * {@code DELETE  /cities/:id} : delete the "id" city.
     *
     * @param id the id of the city to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cities/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        log.debug("REST request to delete City : {}", id);
        cityService.delete(id);
        return ResponseEntity
                .noContent()
                .build();
    }

}
