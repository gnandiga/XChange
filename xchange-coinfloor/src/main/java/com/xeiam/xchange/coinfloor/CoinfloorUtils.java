/**
 * Copyright (C) 2012 - 2014 Xeiam LLC http://xeiam.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.xeiam.xchange.coinfloor;

import com.xeiam.xchange.utils.Base64;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * @author gnandiga
 */
public class CoinfloorUtils {

  private static final ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp224k1");
  private static final ECDomainParameters secp224k1 = new ECDomainParameters(spec.getCurve(), spec.getG(), spec.getN(), spec.getH());

  public static String getNonce() {

    return Base64.encodeBytes(buildNonceString().getBytes());
  }

  protected static String buildNonceString() {

    long currentTime = System.currentTimeMillis();
    return "::" + currentTime + ":";
  }

  public static List<String> getSignatures() {

    byte[] digest = new byte[28];
    ECDSASigner signer = new ECDSASigner();
    signer.init(true, new ECPrivateKeyParameters(new BigInteger(1, digest), secp224k1));
    BigInteger[] signatures = signer.generateSignature(digest);

    return Arrays.asList(Base64.encodeBytes(signatures[0].toByteArray()), Base64.encodeBytes(signatures[1].toByteArray()));
  }
}
