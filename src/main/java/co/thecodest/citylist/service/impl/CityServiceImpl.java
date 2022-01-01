package co.thecodest.citylist.service.impl;

import co.thecodest.citylist.domain.City;
import co.thecodest.citylist.repository.CityRepository;
import co.thecodest.citylist.service.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CityServiceImpl implements CityService {

    private final Logger log = LoggerFactory.getLogger(CityServiceImpl.class);

    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public City save(City city) {
        log.debug("Request to save City : {}", city);
        return cityRepository.save(city);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<City> findAllByName(String name, Pageable pageable) {
        log.debug("Request to get all Cities");
        String nameSearch = null;
        if (!name.isBlank()) {
            nameSearch = "%" + name + "%";
        }
        return cityRepository.findAllByName(nameSearch, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<City> findOne(Long id) {
        log.debug("Request to get City : {}", id);
        return cityRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete City : {}", id);
        this.cityRepository.deleteById(id);
    }
}
