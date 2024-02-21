package io.flowpay.flowpayinterview.repository;

import io.flowpay.flowpayinterview.model.entity.Company;
import io.flowpay.flowpayinterview.model.entity.Representative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Company} entities.
 * Provides methods to perform operations on the database for Company entities.
 */
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * Finds companies by their name.
     *
     * @param name The name of the companies to find.
     * @return A list of companies with the given name.
     */
    List<Company> findAllByName(String name);

    /**
     * Finds companies that have no representatives.
     *
     * @return A list of companies without any representatives.
     */
    // This method might need a custom implementation if the method name parsing does not achieve the desired query.
    List<Company> findAllByRepresentativesEmpty();

    /**
     * Finds companies by representative ID.
     *
     * @param representativeId The ID of the representative associated with the companies.
     * @return A list of companies associated with the given representative ID.
     */
    @Query("SELECT c FROM Company c JOIN c.representatives r WHERE r.id = :representativeId")
    List<Company> findCompaniesByRepresentativeId(@Param("representativeId") Long representativeId);

    /**
     * Disassociates a representative from all companies based on the representative's ID.
     * This method directly modifies the relationship in the database.
     *
     * @param representativeId The ID of the representative to disassociate.
     */
    @Modifying
    @Query("UPDATE Company c SET c.representatives = c.representatives WHERE :representativeId NOT MEMBER OF c.representatives")
    void disassociateRepresentativeFromAllCompanies(@Param("representativeId") Long representativeId);
}
