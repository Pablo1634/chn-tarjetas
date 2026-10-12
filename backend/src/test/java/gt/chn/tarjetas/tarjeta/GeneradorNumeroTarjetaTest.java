package gt.chn.tarjetas.tarjeta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GeneradorNumeroTarjetaTest {

    private GeneradorNumeroTarjeta generador;

    @BeforeEach
    void preparar() {
        generador = new GeneradorNumeroTarjeta();
    }

    @Test
    void generarDevuelveDieciseisDigitos() {
        String numero = generador.generar();

        assertEquals(16, numero.length());
        assertTrue(numero.matches("[0-9]{16}"));
    }

    @Test
    void generarNoDevuelveUnNumeroQueEmpieceEnCero() {
        String numero = generador.generar();

        assertTrue(numero.charAt(0) >= '1');
        assertTrue(numero.charAt(0) <= '9');
    }

    @Test
    void generarPuedeProducirNumerosDistintos() {
        String primerNumero = generador.generar();
        String segundoNumero = generador.generar();

        assertTrue(!primerNumero.equals(segundoNumero));
    }
}
