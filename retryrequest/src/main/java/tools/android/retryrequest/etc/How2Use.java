package tools.android.retryrequest.etc;

import tools.android.retryrequest.Result;
import tools.android.retryrequest.RetryRequest;

public class How2Use {

    void run() {
        String url = "";
        RetryRequest.get()
                .setEnableLogcat(true)
                .setLogtag("RRR")
                .request(url, new Result<Content>() {
                    @Override
                    public void onSuccess(Content content) {

                    }

                    @Override
                    public void onFailure(int code) {

                    }
                }, Content.class);
    }

    class Content {
        String id;
    }
}
