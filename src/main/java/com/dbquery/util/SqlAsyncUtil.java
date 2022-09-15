package com.dbquery.util;

import com.dbquery.datasource.DynamicDataSource;
import com.dbquery.service.SqlService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author yuanguangxin
 */
public class SqlAsyncUtil {

    private static final String EMPTY_SET = "Empty set";

    public static List<LinkedHashMap<String, Object>> callAndGet(String key, Integer dbNum, List<Integer> expectNum, SqlService sqlService,
                                                                 Integer env, String sql, HttpSession session) {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-call-runner-" + key + "-%d").build();
        ExecutorService es = new ThreadPoolExecutor(1, dbNum, 180L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), namedThreadFactory);
        try {
            List<LinkedHashMap<String, Object>> result = new ArrayList<>();
            List<Future<List<LinkedHashMap<String, Object>>>> futureList = new ArrayList<>();
            for (int i = 0; i < dbNum; i++) {
                if (expectNum.contains(i)) {
                    continue;
                }
                futureList.add(es.submit(new CallableTask(i, sqlService, env, sql, session)));
            }
            while (futureList.size() > 0) {
                Iterator<Future<List<LinkedHashMap<String, Object>>>> iterable = futureList.iterator();
                while (iterable.hasNext()) {
                    Future<List<LinkedHashMap<String, Object>>> future = iterable.next();
                    if (future.isDone() && !future.isCancelled()) {
                        List<LinkedHashMap<String, Object>> temp = future.get();
                        result.addAll(temp);
                        iterable.remove();
                    } else {
                        Thread.sleep(1);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("thread-call-runner-" + key + " error", e);
        } finally {
            es.shutdown();
        }
    }

    static class CallableTask implements Callable<List<LinkedHashMap<String, Object>>> {
        private final Integer index;
        private final SqlService sqlService;
        private final Integer env;
        private final String sql;
        private final HttpSession session;


        public CallableTask(Integer index, SqlService sqlService, Integer env, String sql, HttpSession session) {
            super();
            this.index = index;
            this.sqlService = sqlService;
            this.env = env;
            this.sql = sql;
            this.session = session;
        }

        @Override
        public List<LinkedHashMap<String, Object>> call() {
            sqlService.switchDb(index);
            sqlService.doUseSql(env, session);
            List<LinkedHashMap<String, Object>> temp = sqlService.doSql(sql, false);
            DynamicDataSource.clear();
            if (!EMPTY_SET.equals(temp.get(0).get("message"))) {
                return temp;
            }
            return new ArrayList<>();
        }
    }
}
