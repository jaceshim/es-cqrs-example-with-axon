package jace.shim;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jaceshim on 2017. 2. 18..
 */
public interface AnimalRepository extends JpaRepository<AnimalQueryObject, String> {
}
