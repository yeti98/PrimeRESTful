package source;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static spark.Spark.get;

public class PrimeService implements Runnable {
    private LoadingCache<Integer, JSONObject> primeCache;

    public void serviceProvider() {
        primeCache = CacheBuilder.newBuilder()
                .maximumSize(100) //max pieces of cache
                .expireAfterAccess(10, TimeUnit.MILLISECONDS) //time
                .expireAfterWrite(20, TimeUnit.MILLISECONDS)
                .build(new CacheLoader<Integer, JSONObject>() {
                    @Override
                    public JSONObject load(Integer integer) throws Exception {
                        JSONObject primeNumber = new JSONObject();
                        primeNumber.put("Prime Number Less Than" + integer, sieve(integer));
                        return primeNumber;
                    }
                });
        get("/prime", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                response.type("application/json");
                System.out.println(Integer.valueOf(request.queryParams("n")));
                return primeCache.get(Integer.valueOf(request.queryParams("n")));
            }
        });
    }

    private List<Long> sieve(Integer str) {
        if (str == null) return null;
        long max = Long.valueOf(str);
        List<Long> prime = new ArrayList<Long>();
        Boolean isPrime[] = new Boolean[(int) max + 5];
        for (int i = 2; i <= max; i++) isPrime[i] = (i % 2 == 0 ? false : true);
        for (int i = 2; i <= (long) Math.sqrt(max); i++) {
            if (isPrime[i] == true) {
                for (int j = i * i; j <= max; j += i) isPrime[j] = false;
            }
        }
        for (int i = 2; i <= max; i++) {
            if (isPrime[i]) {
                prime.add(Long.valueOf(i));
            }
        }
        return prime;
    }

    public void run() {
        serviceProvider();
    }
}
