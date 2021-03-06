package gov.va.api.health.dataquery.service.controller.condition;

import gov.va.api.health.autoconfig.logging.Loggable;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Loggable
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public interface ConditionRepository extends PagingAndSortingRepository<ConditionEntity, String> {
  Page<ConditionEntity> findByIcn(String icn, Pageable pageable);

  Page<ConditionEntity> findByIcnAndCategory(String icn, String category, Pageable pageable);

  Page<ConditionEntity> findByIcnAndClinicalStatusIn(
      String icn, Set<String> clinicalStatus, Pageable pageable);
}
