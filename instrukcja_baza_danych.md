# Instrukcja tworzenia bazy danych dla aplikacji

## Krok 1: Utwórz bazę danych
```sql
CREATE DATABASE FiszkiDB;
go

## Krok 2: Utwórz tabele
```sql
CREATE TABLE kategorie (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nazwa NVARCHAR(100) NOT NULL
);
go

CREATE TABLE slowka (
    id INT IDENTITY(1,1) PRIMARY KEY,
    polski VARCHAR(255) NOT NULL,
    hiszpanski VARCHAR(255) NOT NULL,
    kategoria_id INT NOT NULL,
    FOREIGN KEY (kategoria_id) REFERENCES kategorie(id)
);
go

## Krok 3: Uzupełnij tabele danymi
```sql
INSERT INTO kategorie (nazwa)
VALUES 
    ('Rodzina'),
    ('Technologia'),
    ('Zawody'),
    ('Dni tygodnia');
go

INSERT INTO slowka (polski, hiszpanski, kategoria_id)
VALUES
    ('aktywować', 'activar', 2),
    ('aktualizacja', 'la actualización', 2),
    ('aktualizować', 'actualizar', 2),
    ('przedłużacz', 'el alargador', 2),
    ('natężenie prądu', 'el amperaje', 2),
    ('automat do gry', 'el arcade', 2),
    ('plik', 'el archivo', 2),
    ('skrót klawiszowy', 'el atajo de teclado', 2),
    ('blokada papieru', 'el atasco de papel', 2),
    ('baza danych', 'la base de datos', 2),
    ('blog', 'el blog', 2),
    ('bloger', 'el blogger', 2),
    ('poczta głosowa', 'el buzón de voz', 2),
    ('kabel', 'el cable', 2),
    ('anulować', 'cancelar', 2),
    ('ładowarka', 'el cargador', 2),
    ('ładować', 'cargar', 2),
    ('folder', 'la carpeta', 2),
    ('słuchawki', 'los cascos', 2),
    ('kopia bezpieczeństwa', 'la copia de seguridad', 2),
    ('podłączyć', 'conectar', 2),
    ('połączenie', 'la conexión', 2),
    ('połączenie automatyczne', 'la conexión automática', 2),
    ('ustawienia konta', 'la configuración de la cuenta', 2),
    ('ustawienia drukarki', 'la configuración de la impresora', 2),
    ('hasło', 'la contraseña', 2),
    ('poczta elektroniczna', 'el correo electrónico', 2),
    ('zapora sieciowa', 'el cortafuegos', 2),
    ('kursor', 'el cursor', 2),
    ('dezaktywować', 'desactivar', 2),
    ('ściągnąć (z Internetu)', 'descargar', 2),
    ('dekoder', 'el descodificador / el decodificador', 2),
    ('odłączyć', 'desconectar / desenchufar', 2),
    ('odłączyć z prądu', 'desenchufar', 2),
    ('odinstalować', 'desinstalar', 2),
    ('adres IP', 'la dirección IP', 2),
    ('adres IP dynamiczny', 'la dirección IP dinámica', 2),
    ('adres IP statyczny', 'la dirección IP estática', 2),
    ('dysk twardy', 'el disco duro', 2),
    ('emulator', 'el emulador', 2),
    ('podłączyć do prądu', 'enchufar', 2),
    ('kontakt', 'el enchufe', 2),
    ('znacznik', 'la etiqueta', 2),
    ('dodatki, rozszerzenia', 'las extensiones', 2),
    ('format', 'el formato', 2),
    ('forum', 'el foro', 2),
    ('źródło zasilania', 'la fuente de alimentación', 2),
    ('nagrywać', 'grabar', 2),
    ('zapisać', 'guardar', 2),
    ('hardware', 'el hardware', 2),
    ('narzędzia', 'las herramientas', 2),
    ('arkusz kalkulacyjny', 'la hoja de cálculo', 2),
    ('login', 'el identificador', 2),
    ('drukarka', 'la impresora', 2),
    ('domyślna drukarka', 'la impresora por defecto', 2),
    ('zainstalować', 'instalar', 2),
    ('Intranet', 'el Intranet', 2),
    ('włączyć aplikację', 'lanzar una aplicación', 2),
    ('pamięć', 'la memoria', 2),
    ('komunikat o błędzie', 'el mensaje de error', 2),
    ('miksować muzykę', 'mezclar música', 2),
    ('model', 'el modelo', 2),
    ('tryb awaryjny', 'el modo seguro', 2),
    ('monitor', 'el monitor', 2),
    ('modem', 'el módem', 2),
    ('przeglądarka internetowa', 'el navegador', 2),
    ('poziom tuszu', 'el nivel de tinta', 2),
    ('komputer', 'el ordenador', 2),
    ('panel sterowania', 'el panel de control', 2),
    ('panel dotykowy', 'la pantalla táctil', 2),
    ('wskazać element kursorem', 'poner el ratón sobre un elemento', 2),
    ('schowek', 'el portapapeles', 2),
    ('laptop', 'el portátil', 2),
    ('pozycjonować strony internetowe', 'posicionar las páginas', 2),
    ('program do nagrywania dźwięku', 'el programa de grabación', 2),
    ('właściwości', 'las propiedades', 2),
    ('bezpiecznik', 'el fusible', 2),
    ('dostawca usług internetowych', 'el proveedor de servicios de Internet', 2),
    ('port USB', 'el puerto USB', 2),
    ('strona testowa', 'la página de prueba', 2),
    ('pusta strona', 'la página en blanco', 2),
    ('strona internetowa', 'la página web', 2),
    ('myszka', 'el ratón', 2),
    ('Sieć', 'la Red', 2),
    ('restart', 'el reinicio', 2),
    ('router', 'el router', 2),
    ('stan mojego konta', 'el saldo de mi cuenta', 2),
    ('oprogramowanie', 'el software', 2),
    ('wolne oprogramowanie', 'el software libre', 2),
    ('tabela', 'la tabla', 2),
    ('tablet', 'la tablet', 2),
    ('klawiatura', 'el teclado', 2),
    ('napięcie elektryczne', 'la tensión eléctrica', 2),
    ('użytkownik', 'el usuario', 2),
    ('szybkość procesora', 'la velocidad del procesador', 2),
    ('wirus', 'el virus', 2),
    ('wyświetlać', 'visionar', 2),
    ('przeglądarka obrazów', 'el visor de fotos', 2),
    ('matka', 'la madre', 1),
    ('ojciec', 'el padre', 1),
    ('ojczym', 'el padrastro', 1),
    ('macocha', 'la madrastra', 1),
    ('syn', 'el hijo', 1),
    ('córka', 'la hija', 1),
    ('brat', 'el hermano', 1),
    ('siostra', 'la hermana', 1),
    ('brat przyrodni', 'el hermanastro', 1),
    ('siostra przyrodnia', 'la hermanastra', 1),
    ('wnuk', 'el nieto', 1),
    ('wnuczka', 'la nieta', 1),
    ('dziadek', 'el abuelo', 1),
    ('babcia', 'la abuela', 1),
    ('pradziadek', 'el bisabuelo', 1),
    ('prababcia', 'la bisabuela', 1),
    ('prapradziadek', 'el tatarabuelo', 1),
    ('praprababcia', 'la tatarabuela', 1),
    ('wujek', 'el tío', 1),
    ('ciotka', 'la tía', 1),
    ('ojciec chrzestny', 'el padrino', 1),
    ('matka chrzestna', 'la madrina', 1),
    ('kuzyn', 'el primo', 1),
    ('kuzynka', 'la prima', 1),
    ('szwagier', 'el cuñado', 1),
    ('szwagierka', 'la cuñada', 1),
    ('teść', 'el suegro', 1),
    ('teściowa', 'la suegra', 1),
    ('zięć', 'el yerno', 1),
    ('synowa', 'la nuera', 1);
    ('poniedziałek', 'lunes', 4),
    ('wtorek', 'martes', 4),
    ('środa', 'miércoles', 4),
    ('czwartek', 'jueves', 4),
    ('piątek', 'viernes', 4),
    ('sobota', 'sábado', 4),
    ('niedziela', 'domingo', 4),
    ('prawnik', 'abogado', 3),
    ('architekt', 'arquitecto', 3),
    ('bibliotekarz', 'bibliotecario', 3),
    ('strażak', 'bombero', 3),
    ('kelner', 'camarero', 3),
    ('listonosz', 'cartero', 3),
    ('kucharz', 'cocinero', 3),
    ('przedsiębiorca', 'empresario', 3),
    ('aptekarz', 'farmacéutico', 3),
    ('fotograf', 'fotógrafo', 3),
    ('inżynier', 'ingeniero', 3),
    ('nauczyciel', 'maestro', 3),
    ('lekarz', 'médico', 3),
    ('piekarz', 'panadero', 3),
    ('fryzjer', 'peluquero', 3),
    ('sekretarz', 'secretario', 3),
    ('weterynarz', 'veterinario', 3),
    ('kierowca autobusu', 'conductor de autobús', 3),
    ('designer', 'diseñador', 3),
    ('doktor', 'doctor', 3),
    ('pisarz', 'escritor', 3),
    ('malarz', 'pintor', 3),
    ('profesor', 'profesor', 3),
    ('programista', 'programador', 3),
    ('tłumacz', 'traductor', 3),
    ('sprzedawca', 'vendedor', 3),
    ('tancerz', 'bailarín', 3),
    ('sędzia', 'juez', 3),
    ('artysta', 'artista', 3),
    ('dentysta', 'dentista', 3),
    ('sportowiec', 'deportista', 3),
    ('stylista', 'estilista', 3),
    ('student', 'estudiante', 3),
    ('krawiec', 'modista', 3),
    ('dziennikarz', 'periodista', 3),
    ('recepcjonista', 'recepcionista', 3),
    ('taksówkarz', 'taxista', 3),
    ('przewodnik wycieczek', 'guía de turismo', 3),
    ('policjant', 'policía', 3),
    ('psychiatra', 'psiquiatra', 3),
    ('astronauta', 'astronauta', 3);
go

## Krok 4: Utwórz procedury

### Dodawanie kategorii
CREATE PROCEDURE DodajKategorie
    @nazwa NVARCHAR(100)
AS
BEGIN
    BEGIN TRY
        INSERT INTO kategorie (nazwa)
        VALUES (@nazwa);
    END TRY
    BEGIN CATCH
        PRINT 'Błąd podczas dodawania kategorii: ' + ERROR_MESSAGE();
    END CATCH
END;
go

### Dodawanie słówek
CREATE PROCEDURE DodajSlowko
    @polski NVARCHAR(255),
    @hiszpanski NVARCHAR(255),
    @kategoria_id INT
AS
BEGIN
    BEGIN TRY
        INSERT INTO slowka (polski, hiszpanski, kategoria_id)
        VALUES (@polski, @hiszpanski, @kategoria_id);
    END TRY
    BEGIN CATCH
        PRINT 'Błąd podczas dodawania słówka: ' + ERROR_MESSAGE();
    END CATCH
END;
go

### Edycja kategorii
CREATE PROCEDURE EdytujKategorie
    @id INT,
    @nowaNazwa NVARCHAR(100)
AS
BEGIN
    BEGIN TRY
        UPDATE kategorie
        SET nazwa = @nowaNazwa
        WHERE id = @id;
    END TRY
    BEGIN CATCH
        PRINT 'Błąd podczas edytowania kategorii: ' + ERROR_MESSAGE();
    END CATCH
END;
go

### Edycja słówek
CREATE PROCEDURE EdytujSlowko
    @id INT,
    @nowyPolski NVARCHAR(255),
    @nowyHiszpanski NVARCHAR(255),
    @nowaKategoria INT
AS
BEGIN
    BEGIN TRY
        UPDATE Slowka
        SET polski = @nowyPolski,
            hiszpanski = @nowyHiszpanski,
            kategoria_id = @nowaKategoria
        WHERE id = @id;
    END TRY
    BEGIN CATCH
        PRINT 'Błąd podczas edycji słówka: ' + ERROR_MESSAGE();
    END CATCH
END;
go

### Usuwanie kategorii
CREATE PROCEDURE UsunKategorie
    @nazwa NVARCHAR(100)
AS
BEGIN
    BEGIN TRY
        DELETE FROM kategorie
        WHERE nazwa = @nazwa;
    END TRY
    BEGIN CATCH
        PRINT 'Błąd podczas usuwania kategorii: ' + ERROR_MESSAGE();
    END CATCH
END;
go

### Usuwanie słówek
CREATE PROCEDURE UsunSlowko
    @polski nvarchar(255),
	@kategoria_id int
AS
BEGIN
    BEGIN TRY
        DELETE FROM Slowka WHERE polski = @polski AND kategoria_id = @kategoria_id;
    END TRY
    BEGIN CATCH
        PRINT 'Błąd podczas usuwania słówka: ' + ERROR_MESSAGE();
    END CATCH
END;
go
