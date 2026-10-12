package gt.chn.tarjetas.tarjeta;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class GeneradorNumeroTarjeta {

    private final SecureRandom generadorAleatorio = new SecureRandom();

    public String generar() {
        StringBuilder numero = new StringBuilder(16);

        numero.append(generadorAleatorio.nextInt(9) + 1);

        for (int posicion = 1; posicion < 16; posicion++) {
            numero.append(generadorAleatorio.nextInt(10));
        }

        return numero.toString();
    }
}