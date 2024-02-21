package io.flowpay.flowpayinterview.repository;

import io.flowpay.flowpayinterview.model.entity.Representative;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

/**
 * Spring Data JPA repository for {@link Representative} entities.
 * Provides methods to perform database operations on Representative entities.
 */
public interface RepresentativeRepository extends JpaRepository<Representative, Long> {

    /**
     * Finds representatives by their first name and last name.
     *
     * @param firstName The first name of the representative(s) to find.
     * @param lastName The last name of the representative(s) to find.
     * @return A list of representatives with the given first name and last name.
     */
    Set<Representative> findAllByFirstNameAndLastName(String firstName, String lastName);
}
