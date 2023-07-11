package com.gihtub.alexeylapin.conreg.json.gson;

import com.gihtub.alexeylapin.conreg.client.http.dto.TokenDto;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class TokenDtoAdapter extends TypeAdapter<TokenDto> {

    @Override
    public void write(JsonWriter out, TokenDto src) throws IOException {
        out.beginObject();
        out.name("token").value(src.getToken());
        out.name("access_token").value(src.getAccessToken());
        out.name("expires_in").value(src.getExpiresIn());
        out.name("issued_at").value(src.getIssuedAt());
        out.endObject();
    }

    @Override
    public TokenDto read(JsonReader in) throws IOException {
        TokenDto target = new TokenDto();

        in.beginObject();
        while (in.hasNext()) {
            String key = in.nextName();

            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                continue;
            }

            switch (key) {
                case "token":
                    target.setToken(in.nextString());
                    break;
                case "access_token":
                    target.setAccessToken(in.nextString());
                    break;
                case "expires_in":
                    target.setExpiresIn(in.nextInt());
                    break;
                case "issued_at":
                    target.setIssuedAt(in.nextString());
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();

        return target;
    }

}
