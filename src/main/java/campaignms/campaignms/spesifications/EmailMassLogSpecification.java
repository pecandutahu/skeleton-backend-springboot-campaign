package campaignms.campaignms.spesifications;
import org.springframework.data.jpa.domain.Specification;

import campaignms.campaignms.models.EmailMassLog;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class EmailMassLogSpecification {

    public static Specification<EmailMassLog> hasColumn(String column, String value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(column), value);
    }

    public static Specification<EmailMassLog> containsColumn(String column, String value) {
        return new Specification<EmailMassLog>() {
            @Override
            public Predicate toPredicate(Root<EmailMassLog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get(column)), "%" + value.toLowerCase() + "%");
            }
        };
    }
}
