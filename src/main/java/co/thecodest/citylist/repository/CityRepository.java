package co.thecodest.citylist.repository;

import co.thecodest.citylist.domain.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    @Query("select c from City c where (:name is null or c.name like :name)")
    Page<City> findAllByName(@Param("name") String name, Pageable pageable);
}
