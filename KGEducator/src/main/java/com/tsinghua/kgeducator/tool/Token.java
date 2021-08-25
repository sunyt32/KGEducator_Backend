package com.tsinghua.kgeducator.tool;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.HashMap;
import java.util.Map;

public class Token {
    private static String TOKEN_SECRET = "i love cat";
    private static Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
    private static JWTVerifier verifier = JWT.require(algorithm).build();

    public static String token(Integer id) {
        String token = "";
        try
        {
            Map<String, Object> header = new HashMap<>();
            header.put("typ", "JWT");
            header.put("alg", "HS256");
            token = JWT.create()
                    .withHeader(header)
                    .withClaim("userId", id)
                    .sign(algorithm);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return token;
    }

    public static boolean verify(String token)
    {
        try
        {
            DecodedJWT jwt = verifier.verify(token);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }
    public static Integer getUserId(String token)
    {
        try
        {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("userId").asInt();
        }
        catch (JWTDecodeException e)
        {
            e.getStackTrace();
        }
        return null;
    }

}
