package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import crm.chifco.com.model.NotificationConfig;

public interface NotificationConfigRepository extends JpaRepository<NotificationConfig, Long> {

  NotificationConfig getBynotificationConfigId(Long configid);

}
