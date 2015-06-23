package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;
import java.util.List;

import catchla.yep.model.util.AWSDateConverter;
import catchla.yep.model.util.ConditionConverter;
import catchla.yep.model.util.ISO8601DateConverter;

/**
 * Created by mariotaku on 15/6/23.
 */
@JsonObject
public class S3UploadToken implements UploadToken {

    @JsonField(name = "provider")
    private String provider;
    @JsonField(name = "options")
    private Options options;

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    public Options getOptions() {
        return options;
    }

    @JsonObject
    public static class Options {
        @JsonField(name = "bucket")
        private String bucket;
        @JsonField(name = "signature")
        private String signature;
        @JsonField(name = "key")
        private String key;
        @JsonField(name = "url")
        private String url;
        @JsonField(name = "policy")
        private Policy policy;
        @JsonField(name = "encoded_policy")
        private String encodedPolicy;

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setPolicy(Policy policy) {
            this.policy = policy;
        }

        public void setEncodedPolicy(String encodedPolicy) {
            this.encodedPolicy = encodedPolicy;
        }

        public String getBucket() {
            return bucket;
        }

        public String getSignature() {
            return signature;
        }

        public String getKey() {
            return key;
        }

        public String getUrl() {
            return url;
        }

        public Policy getPolicy() {
            return policy;
        }

        public String getEncodedPolicy() {
            return encodedPolicy;
        }

    }

    @JsonObject
    public static class Policy {
        @JsonField(name = "expiration", typeConverter = AWSDateConverter.class)
        private Date expiration;
        @JsonField(name = "conditions")
        private List<Condition> conditions;

        public void setExpiration(Date expiration) {
            this.expiration = expiration;
        }

        public void setConditions(List<Condition> conditions) {
            this.conditions = conditions;
        }

        public Date getExpiration() {
            return expiration;
        }

        public List<Condition> getConditions() {
            return conditions;
        }

    }

    public static class Condition {
        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }
    }
}
