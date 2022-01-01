package co.thecodest.citylist.web.rest;

import co.thecodest.citylist.IntegrationTest;
import co.thecodest.citylist.domain.City;
import co.thecodest.citylist.repository.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
public class CityResourceIT {

    private static final String DEFAULT_NAME = "PARIS";
    private static final String UPDATED_NAME = "BERLIN";

    private static final String DEFAULT_PHOTO = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/La_Tour_Eiffel_vue_de_la_Tour_Saint-Jacques%2C_Paris_ao%C3%BBt_2014_%282%29.jpg/500px-La_Tour_Eiffel_vue_de_la_Tour_Saint-Jacques%2C_Paris_ao%C3%BBt_2014_%282%29.jpg";
    private static final String UPDATED_PHOTO = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a6/Cityenburger_Tor_abends.jpg/500px-Cityenburger_Tor_abends.jpg";

    private static final String ENTITY_API_URL = "/api/cities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCityMockMvc;

    private City city;


    public static City createEntity(EntityManager em) {
        final City city = new City();
        city.setName(DEFAULT_NAME);
        city.setPhoto(DEFAULT_PHOTO);
        return city;
    }


    @BeforeEach
    public void initTest() {
        city = createEntity(em);
    }

    @Test
    @Transactional
    void createCity() throws Exception {
        int databaseSizeBeforeCreate = cityRepository.findAll().size();
        restCityMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(city)))
                .andExpect(status().isCreated());

        List<City> cityList = cityRepository.findAll();
        assertThat(cityList).hasSize(databaseSizeBeforeCreate + 1);
        City testCity = cityList.get(cityList.size() - 1);
        assertThat(testCity.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCity.getPhoto()).isEqualTo(DEFAULT_PHOTO);
    }

    @Test
    @Transactional
    void createCityWithExistingId() throws Exception {
        city.setId(1L);

        int databaseSizeBeforeCreate = cityRepository.findAll().size();

        restCityMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(city)))
                .andExpect(status().isBadRequest());

        List<City> cityList = cityRepository.findAll();
        assertThat(cityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCities() throws Exception {
        cityRepository.saveAndFlush(city);

        restCityMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(city.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
                .andExpect(jsonPath("$.[*].photo").value(hasItem(DEFAULT_PHOTO)));
    }

    @Test
    @Transactional
    void getCity() throws Exception {
        cityRepository.saveAndFlush(city);

        restCityMockMvc
                .perform(get(ENTITY_API_URL_ID, city.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(city.getId().intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
                .andExpect(jsonPath("$.photo").value(DEFAULT_PHOTO));
    }

    @Test
    @Transactional
    void getNonExistingCity() throws Exception {
        restCityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCity() throws Exception {
        cityRepository.saveAndFlush(city);

        int databaseSizeBeforeUpdate = cityRepository.findAll().size();

        City updatedCity = cityRepository.findById(city.getId()).get();

        em.detach(updatedCity);
        updatedCity.setName(UPDATED_NAME);
        updatedCity.setPhoto(UPDATED_PHOTO);

        restCityMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, updatedCity.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(updatedCity))
                )
                .andExpect(status().isOk());

        List<City> cityList = cityRepository.findAll();
        assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
        City testCity = cityList.get(cityList.size() - 1);
        assertThat(testCity.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCity.getPhoto()).isEqualTo(UPDATED_PHOTO);
    }

    @Test
    @Transactional
    void putNonExistingCity() throws Exception {
        int databaseSizeBeforeUpdate = cityRepository.findAll().size();
        city.setId(count.incrementAndGet());

        restCityMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, city.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(city))
                )
                .andExpect(status().isBadRequest());

        List<City> cityList = cityRepository.findAll();
        assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCity() throws Exception {
        int databaseSizeBeforeUpdate = cityRepository.findAll().size();
        city.setId(count.incrementAndGet());

        restCityMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, count.incrementAndGet())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(city))
                )
                .andExpect(status().isBadRequest());

        List<City> cityList = cityRepository.findAll();
        assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCity() throws Exception {
        int databaseSizeBeforeUpdate = cityRepository.findAll().size();
        city.setId(count.incrementAndGet());

        restCityMockMvc
                .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(city)))
                .andExpect(status().isMethodNotAllowed());

        List<City> cityList = cityRepository.findAll();
        assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
    }
    
    @Test
    @Transactional
    void patchWithMissingIdPathParamCity() throws Exception {
        int databaseSizeBeforeUpdate = cityRepository.findAll().size();
        city.setId(count.incrementAndGet());

        restCityMockMvc
                .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(city)))
                .andExpect(status().isMethodNotAllowed());

        List<City> cityList = cityRepository.findAll();
        assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCity() throws Exception {
        cityRepository.saveAndFlush(city);

        int databaseSizeBeforeDelete = cityRepository.findAll().size();

        restCityMockMvc
                .perform(delete(ENTITY_API_URL_ID, city.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        List<City> cityList = cityRepository.findAll();
        assertThat(cityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
