package io.flowpay.flowpayinterview.config;

/**
 * Constants for API endpoint URLs.
 * This class centralizes the management of endpoint paths to ensure consistency
 * and ease the maintenance of the API structure.
 */
public class ApiUrls {

    // Base endpoint for companies-related operations
    public static final String URL_COMPANIES = "/api/companies";
    // Endpoint for specific company operations by ID
    public static final String URL_COMPANIES_BY_ID = "/{id}";
    // Endpoint for fetching companies by name
    public static final String URL_COMPANIES_BY_NAME = "/name/{name}";
    // Endpoint for listing companies without any representatives
    public static final String URL_COMPANIES_WITHOUT_REPRESENTATIVE = "/no-representative";
    // Endpoint for fetching all companies
    public static final String URL_COMPANIES_ALL = "/all";
    // Endpoint for listing all representatives of a specific company
    public static final String URL_COMPANY_REPRESENTATIVES_ALL = "/{companyId}/representatives";
    // Endpoint for assigning a representative to a company
    public static final String URL_ASSIGN_REPRESENTATIVE_TO_COMPANY = "/{companyId}/representatives/{representativeId}/assign";
    // Endpoint for unassigning a representative from a company
    public static final String URL_UNASSIGN_REPRESENTATIVE_FROM_COMPANY = "/{companyId}/representatives/{representativeId}/unassign";
    // Endpoint for transferring a representative between companies
    public static final String URL_TRANSFER_REPRESENTATIVE_BETWEEN_COMPANIES = "/transfer/representative/";

    // Base endpoint for representatives-related operations
    public static final String URL_REPRESENTATIVES = "/api/representatives";
    // Endpoint for specific representative operations by ID
    public static final String URL_REPRESENTATIVES_BY_ID = "/{id}";
    // Endpoint for fetching representatives by first and last name
    public static final String URL_REPRESENTATIVES_BY_FIRST_AND_LAST_NAME = "/name";
    // Endpoint for fetching all representatives
    public static final String URL_REPRESENTATIVES_ALL = "/all";
}
