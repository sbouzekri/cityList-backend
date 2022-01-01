package co.thecodest.citylist.service;

import co.thecodest.citylist.domain.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CityService {
    /**
     * Save a city.
     *
     * @param city the entity to save.
     * @return the persisted entity.
     */
    City save(City city);

    /**
     * Get all the cities.
     *
     * @return the list of cities.
     */
    Page<City> findAllByName(String name, Pageable pageable);

    /**
     * Get the "id" city.
     *
     * @param id the id of the city.
     * @return the city.
     */
    Optional<City> findOne(Long id);

    /**
     * Delete the "id" city.
     *
     * @param id the id of the city.
     */
    void delete(Long id);
}
