package th.in.ffc.airsync.api.services.dao;

import th.in.ffc.airsync.api.services.Pcu;

public interface DaoDataAccess {
    boolean insertPcu(Pcu pcu);
    boolean updatePcuStatus(Pcu pcu);

}
