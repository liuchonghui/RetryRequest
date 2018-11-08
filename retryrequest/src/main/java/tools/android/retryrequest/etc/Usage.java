package tools.android.retryrequest.etc;

import tools.android.retryrequest.Result;
import tools.android.retryrequest.RetryRequest;

class Usage {

    public void run() {
        String url = "";
        RetryRequest.get()
                .setEnableLogcat(true)
                .setLogtag("RRR")
                .setDelayMillis(3333L)
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
