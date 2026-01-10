package task_11.db.dao;

import di.Inject;
import task_11.db.TransactionManager;
import task_11.db.interfaces.BaseRepository;
import task_11.exceptions.dao.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<T> extends BaseDao implements BaseRepository<T> {

}
