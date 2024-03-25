package com.ventimetriconsulting;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto.BranchResponseEntity;
import com.ventimetriconsulting.branch.controller.BranchController;
import com.ventimetriconsulting.branch.entity.dto.BranchCreationEntity;
import com.ventimetriconsulting.branch.entity.dto.BranchType;
import com.ventimetriconsulting.inventario.controller.StorageController;
import com.ventimetriconsulting.inventario.entity.dto.StorageDTO;
import com.ventimetriconsulting.supplier.controller.SupplierController;
import com.ventimetriconsulting.supplier.dto.ProductDTO;
import com.ventimetriconsulting.supplier.dto.SupplierDTO;
import com.ventimetriconsulting.supplier.entity.UnitMeasure;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class DataLoader implements CommandLineRunner {

    private BranchController branchController;
    private SupplierController supplierController;
    private StorageController storageController;

    @Autowired
    public DataLoader(BranchController branchController, SupplierController supplierController, StorageController storageController) {
        this.branchController = branchController;
        this.supplierController = supplierController;
        this.storageController = storageController;
    }

    @Override
    public void run(String... args) throws Exception {
        String userCode = "0000000000";

        BranchCreationEntity fakeBranch = BranchCreationEntity.builder()
                .branchCode("")
                .userCode(userCode)
                .name("20m2 Cisternino")
                .email("samplebranch@example.com")
                .address("123 Fake Street")
                .city("Faketown")
                .cap("12345")
                .phoneNumber("555-1234")
                .vat("VAT123456")
                .type(BranchType.RESTAURANT)
                .logoImage(new byte[]{})
                .build();

        ResponseEntity<BranchResponseEntity> savedBranch = branchController.save(fakeBranch);

        ResponseEntity<StorageDTO> cisterninoStorage = storageController.addStorage(StorageDTO.builder()
                        .storageId(0)
                        .creationTime(new Date())
                        .name("Magazzino 20m2")
                        .city("Cisternino")
                        .address("Via 4 novembre 8")
                        .cap("72914")
                        .build(),
                Objects.requireNonNull(savedBranch.getBody()).getBranchCode());

        List<SupplierDTO> supplierDTOS = retrieveSupplierList();

        for(SupplierDTO supplierDTO : supplierDTOS){
            ResponseEntity<SupplierDTO> supplierDTOResponseEntity = supplierController
                    .addSupplier(supplierDTO,
                            Objects.requireNonNull(savedBranch.getBody()).getBranchCode());

            ResponseEntity<List<ProductDTO>> listProd = supplierController
                    .insertProductList(getPRoductListById(Integer.parseInt(supplierDTO.getVatNumber())),
                    Objects.requireNonNull(supplierDTOResponseEntity.getBody()).getSupplierId());

//            if(Objects.equals(supplierDTOResponseEntity.getBody().getName(), "Magazzino 20m2")){
//                for(ProductDTO productDTO : Objects.requireNonNull(listProd.getBody())) {
//                    System.out.println("add prod " + productDTO.toString());
//                    storageController.appProduct(productDTO,
//                            Objects.requireNonNull(cisterninoStorage.getBody()).getStorageId(),
//                            "amati.angelo90@gmail.com");
//                }
//            }
        }
    }

    public static List<SupplierDTO> retrieveSupplierList() {

        List<SupplierDTO> suppliersList = new ArrayList<>();
        suppliersList.add(new SupplierDTO(0, "Magazzino 20m2", "463", "", "", "", "", "", "", "", "", null));
        suppliersList.add(new SupplierDTO(0, "Angelo simeone", "464", "", "", "", "", "+393388004773", "simeoneangelo1979@libero.it", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Mattia", "466", "", "", "", "", "3803268119", "mattialiuzzi@hotmail.it", "","Italia", null));
        suppliersList.add(new SupplierDTO(0, "Santoro", "467", "", "", "35480", "", "3454937840", "amati.angelo90@gmail.com", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Fornitore Esempio", "468", "via roma 34", "Cisternino", "72014", "", "3454937077", "esempiofornitore@gmail.com", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Piero Montenegro", "469", "", "", "", "", "+393285860885", "piero.greco.ext@montenegro.it", "", "Italia",null));
        suppliersList.add(new SupplierDTO(0, "Giuseppe Martini", "470", "", "", "", "", "3459515136", "gangelillo@bacardi.com", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Fabio gal", "471", "", "", "", "", "3339101487", "galbibite@libero.it", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Alberto Velier", "472", "", "", "", "", "3485174174", "albertobiunno@gmail.com", "", "Italia",null));
        suppliersList.add(new SupplierDTO(0, "Mimmo Sifor", "473", "", "", "", "", "3382603861", "siforsrl@libero.it", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Gianluca sanitec", "474", "", "", "", "", "3334145027", "gianlucacasio@euroshopsrl.it", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Andrea Taveri", "475", "", "", "", "", "3208193807", "mattialiuzzi@hotmail.it", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Luciano Tormaresca", "476", "", "", "", "", "336274885", "carbone.luc@gmail.com", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Deborah sagna", "477", "", "", "", "", "3398820588", "debora@agenziatres.it", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Pastini", "478", "", "", "", "", "3929944659", "info@ipastini.it", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Donato Panini", "479", "via clarizia", "", "72014", "05173382", "3202518517", "valelui1408@yahoo.it", "", "Italia",null));
        suppliersList.add(new SupplierDTO(0, "Macelleria longo", "480", "", "", "", "", "3927043155", "angelopizzutoli87@gmail.com", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Domenico aia", "481", "", "", "", "", "3920730621", "domenico.pace@aia-spa.it", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Salumificio Santoro", "482", "", "", "", "", "0804431297", "ordini@salumificiodantoro.it", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Andrea Formaggi", "483", "", "", "", "", "3803632793", "info@anticaricettamartinese.it", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Cardone", "484", "", "", "", "", "3887350884", "Marianna.cardone@cardonevini.com", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "giacomoko", "485", "", "", "", "", "14784845448", "amaticorporation@gmail.com", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Fornitoreextra", "486", "", "", "", "", "3803268199", "mattialiuzzi@hotmail.it", "", "Italia",  null));
        suppliersList.add(new SupplierDTO(0, "Frutta", "487", "", "", "", "", "3803268119", "toninocontinisio@libero.it", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Pesce", "488", "", "", "", "", "3358188937", "Giovanni.mancini@leporemare.com", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "De giorgio bevande", "489", "", "", "", "", "3495038350", "nicolanisi@degiorgiobeverage.it", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Nicola Arnese", "490", "", "", "", "", "3200555306", "nicola695@libero.it", "", "Italia", null));
        suppliersList.add(new SupplierDTO(0, "Nicola dolci", "491", "", "", "", "", "3200555306", "nicola695@libero.it", "", "Italia", null));


        return suppliersList;
    }

    private List<ProductDTO> getPRoductListById(int id){

        List<ProductDTO> listProduct = new ArrayList<>();
        switch (id){
            case 463:
                listProduct.add(new ProductDTO(0, "Hamburger di manzo", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Hamburger ripieno", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Pollo croccante", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Tagliata di pollo", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Tagliata di manzo", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Tartare di manzo", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Tartare di tonno", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Polpo", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Stracciata di melanzane", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Polpette di carne", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Polpette di pane", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Pulled pork", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Cipolla caramellata", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Patate al forno", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Guacamole", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa poke 20m2", "", UnitMeasure.LITRI, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa burger 20m2", "", UnitMeasure.LITRI, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Maio sweet chili", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Maio teriaki", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Sauce cheddar", "", UnitMeasure.LITRI, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Maio Japan", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Maio plic plac", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Maio rosmarino", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Bombette", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Focaccia", "", UnitMeasure.ALTRO, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Crema mascarpone", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Tiramisù", "", UnitMeasure.PEZZI, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Salame al cioccolato", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Cheesecake", "", UnitMeasure.PEZZI, "", 0, 0, "", ""));
                break;
            case 464:
                listProduct.add(new ProductDTO(0, "Mozzarella", "", UnitMeasure.KG, "", 4, 6.9, "", ""));
                listProduct.add(new ProductDTO(0, "Stracciatella", "", UnitMeasure.UNITA, "", 10, 3.3, "", "8.10"));
                listProduct.add(new ProductDTO(0, "Bufala", "", UnitMeasure.KG, "", 10, 12.8, "", ""));
                listProduct.add(new ProductDTO(0, "Silano affumicato", "", UnitMeasure.KG, "", 4, 9.5, "", ""));
                listProduct.add(new ProductDTO(0, "Cacioricotta", "", UnitMeasure.UNITA, "", 0, 9.5, "", ""));
                listProduct.add(new ProductDTO(0, "Mozzarella affumicata", "", UnitMeasure.KG, "", 4, 6.9, "", ""));
                listProduct.add(new ProductDTO(0, "Ricotta", "", UnitMeasure.UNITA, "", 4, 2, "", ""));
                listProduct.add(new ProductDTO(0, "Burratina", "", UnitMeasure.UNITA, "", 10, 0, "", ""));
                break;
            case 466:
                listProduct.add(new ProductDTO(0, "Lattuga Iceberg", "", UnitMeasure.KG, "", 10, 2, "", ""));
                listProduct.add(new ProductDTO(0, "Tonno in Scatola", "", UnitMeasure.PACCHI, "", 10, 5.85, "", "a wx"));
                break;
            case 467:
                listProduct.add(new ProductDTO(0, "pane", "", UnitMeasure.KG, "", 4, 1, "", ""));
                listProduct.add(new ProductDTO(0, "ghiaccio", "", UnitMeasure.KG, "", 4, 0.75, "", ""));
                listProduct.add(new ProductDTO(0, "Soda", "", UnitMeasure.LITRI, "", 4, 1.75, "", ""));
                listProduct.add(new ProductDTO(0, "sale", "", UnitMeasure.KG, "", 5, 45.85, "", "acag"));
                break;
            case 468:
                listProduct.add(new ProductDTO(0, "Aperol", "", UnitMeasure.LITRI, "", 4, 11.58, "", ""));
                listProduct.add(new ProductDTO(0, "Gin", "", UnitMeasure.LITRI, "", 4, 12.85, "", ""));
                listProduct.add(new ProductDTO(0, "Peroni 33cl", "", UnitMeasure.BOTTIGLIA, "", 4, 0.25, "", ""));
                listProduct.add(new ProductDTO(0, "Prosecco doc", "", UnitMeasure.LITRI, "", 4, 12.52, "", ""));
                break;
            case 469:
                listProduct.add(new ProductDTO(0, "Select", "", UnitMeasure.LITRI, "", 0, 6.71, "", ""));
                listProduct.add(new ProductDTO(0, "Rosso antico", "", UnitMeasure.LITRI, "", 0, 6.5, "", ""));
                listProduct.add(new ProductDTO(0, "Belenkaya", "", UnitMeasure.LITRI, "", 0, 7.7, "", ""));
                listProduct.add(new ProductDTO(0, "Matusalem", "", UnitMeasure.LITRI, "", 0, 10.5, "", ""));
                listProduct.add(new ProductDTO(0, "Matusalem anejo", "", UnitMeasure.LITRI, "", 0, 12.6, "", ""));
                listProduct.add(new ProductDTO(0, "Beluga", "", UnitMeasure.LITRI, "", 0, 20.13, "", ""));
                listProduct.add(new ProductDTO(0, "Jose cuervo silver", "", UnitMeasure.LITRI, "", 0, 13.67, "", ""));
                listProduct.add(new ProductDTO(0, "Edgar", "", UnitMeasure.BOTTIGLIA, "", 0, 8.54, "", ""));
                listProduct.add(new ProductDTO(0, "Beluga 1,5", "", UnitMeasure.BOTTIGLIA, "", 22, 71.37, "", ""));
                listProduct.add(new ProductDTO(0, "Beluga 3 ", "", UnitMeasure.UNITA, "", 22, 150, "", ""));
                listProduct.add(new ProductDTO(0, "Beluga 6 ", "", UnitMeasure.UNITA, "", 22, 361, "", ""));
                listProduct.add(new ProductDTO(0, "Montenegro", "", UnitMeasure.BOTTIGLIA, "", 22, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Dâ€™arapri", "", UnitMeasure.BOTTIGLIA, "", 22, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Dâ€™arapri rose", "", UnitMeasure.BOTTIGLIA, "", 22, 1, "", ""));
                break;
            case 470:
                listProduct.add(new ProductDTO(0, "Bombay dry", "", UnitMeasure.LITRI, "", 0, 10.5, "", ""));
                listProduct.add(new ProductDTO(0, "Bacardi blanco", "", UnitMeasure.LITRI, "", 0, 10.37, "", ""));
                listProduct.add(new ProductDTO(0, "Grey goose", "", UnitMeasure.LITRI, "", 0, 23.15, "", ""));
                listProduct.add(new ProductDTO(0, "Bombay saphire 70", "", UnitMeasure.LITRI, "", 0, 13.22, "", ""));
                listProduct.add(new ProductDTO(0, "Patron silver", "", UnitMeasure.LITRI, "", 0, 33, "", ""));
                listProduct.add(new ProductDTO(0, "Patron reposado", "", UnitMeasure.LITRI, "", 0, 39.72, "", ""));
                listProduct.add(new ProductDTO(0, "Patron anejo", "", UnitMeasure.LITRI, "", 0, 41.24, "", ""));
                listProduct.add(new ProductDTO(0, "Bacardi oro", "", UnitMeasure.LITRI, "", 0, 12.26, "", ""));
                break;
            case 471:
                listProduct.add(new ProductDTO(0, "Tonica 1 ", "", UnitMeasure.LITRI, "", 0, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Lemon 1", "", UnitMeasure.BOTTIGLIA, "", 0, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Keglevic pesca", "", UnitMeasure.BOTTIGLIA, "", 0, 9.64, "", ""));
                listProduct.add(new ProductDTO(0, "Prosecco", "", UnitMeasure.BOTTIGLIA, "", 0, 3.1, "", ""));
                listProduct.add(new ProductDTO(0, "Ginger beer fever", "", UnitMeasure.BOTTIGLIETTA, "", 0, 1.13, "", ""));
                listProduct.add(new ProductDTO(0, "Aperol", "", UnitMeasure.LITRI, "", 0, 11.6, "", ""));
                listProduct.add(new ProductDTO(0, "Triple sec bols", "", UnitMeasure.LITRI, "", 0, 9.64, "", ""));
                listProduct.add(new ProductDTO(0, "Passoa", "", UnitMeasure.BOTTIGLIA, "", 0, 11.9, "", ""));
                listProduct.add(new ProductDTO(0, "Red bull", "", UnitMeasure.ALTRO, "lattina", 0, 1.14, "", ""));
                listProduct.add(new ProductDTO(0, "Tonica vap", "", UnitMeasure.BOTTIGLIETTA, "", 0, 0.5, "", ""));
                listProduct.add(new ProductDTO(0, "Lemon vap", "", UnitMeasure.BOTTIGLIETTA, "", 0, 0.5, "", ""));
                listProduct.add(new ProductDTO(0, "Premix fragola", "", UnitMeasure.BOTTIGLIA, "", 0, 11, "", ""));
                listProduct.add(new ProductDTO(0, "Coca vap", "", UnitMeasure.BOTTIGLIETTA, "", 0, 0.56, "", ""));
                listProduct.add(new ProductDTO(0, "Zucchero liquido", "", UnitMeasure.BOTTIGLIA, "", 0, 5, "", ""));
                listProduct.add(new ProductDTO(0, "Succo ananas piccolo", "", UnitMeasure.BOTTIGLIETTA, "", 0, 0.62, "", ""));
                listProduct.add(new ProductDTO(0, "Succo arancia piccolo", "", UnitMeasure.BOTTIGLIETTA, "", 0, 0.62, "", ""));
                listProduct.add(new ProductDTO(0, "Midori", "", UnitMeasure.BOTTIGLIA, "", 0, 13, "", ""));
                listProduct.add(new ProductDTO(0, "Acqua fardello", "", UnitMeasure.FARDELLO, "", 0, 4, "", ""));
                listProduct.add(new ProductDTO(0, "Soda 2", "", UnitMeasure.BOTTIGLIA, "", 0, 4, "", ""));
                listProduct.add(new ProductDTO(0, "Acqua naturale fardello", "", UnitMeasure.FARDELLO, "", 0, 4, "", ""));
                listProduct.add(new ProductDTO(0, "Acqua frizzante fardello", "", UnitMeasure.FARDELLO, "", 0, 4, "", ""));
                listProduct.add(new ProductDTO(0, "Coca cola 1lt", "", UnitMeasure.BOTTIGLIA, "", 0, 1.1, "", ""));
                listProduct.add(new ProductDTO(0, "Belvedere", "", UnitMeasure.BOTTIGLIA, "", 0, 28.06, "", ""));
                listProduct.add(new ProductDTO(0, "Amaretto disaronno", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Ginger beer schewpps", "", UnitMeasure.BOTTIGLIA, "", 22, 0.5, "", ""));
                listProduct.add(new ProductDTO(0, "Mezcal verde", "", UnitMeasure.BOTTIGLIA, "", 22, 29.28, "", ""));
                listProduct.add(new ProductDTO(0, "Lemon soda lattina", "", UnitMeasure.UNITA, "", 22, 0.44, "", ""));
                listProduct.add(new ProductDTO(0, "Branca menta", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Antica formula", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Vermouth cocchi", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Vermouth madame", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Kinobi", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Cramberry succo", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Campari", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Nastro", "", UnitMeasure.CARTONI, "", 22, 18, "", ""));
                listProduct.add(new ProductDTO(0, "Peroni", "", UnitMeasure.CARTONI, "", 22, 13.5, "", ""));
                listProduct.add(new ProductDTO(0, "Tennets", "", UnitMeasure.CARTONI, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Ichnusa", "", UnitMeasure.CARTONI, "", 22, 18.91, "", ""));
                listProduct.add(new ProductDTO(0, "Montenegro", "", UnitMeasure.BOTTIGLIA, "", 22, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Darapri", "", UnitMeasure.BOTTIGLIA, "", 22, 1, "", ""));
                listProduct.add(new ProductDTO(0, "darapri rose", "", UnitMeasure.BOTTIGLIA, "", 22, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Tonica fever", "", UnitMeasure.BOTTIGLIA, "", 22, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Malibu", "", UnitMeasure.BOTTIGLIA, "", 22, 13, "", ""));
                listProduct.add(new ProductDTO(0, "Acqua frizzante grande", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                break;
            case 472:
                listProduct.add(new ProductDTO(0, "Hendricks 70", "", UnitMeasure.LITRI, "", 0, 25.9, "", ""));
                listProduct.add(new ProductDTO(0, "Sweet&sour", "", UnitMeasure.ALTRO, "bustina", 0, 3, "", ""));
                break;
            case 473:
                listProduct.add(new ProductDTO(0, "Bicchieri 350", "", UnitMeasure.UNITA, "", 0, 0.1, "", ""));
                break;
            case 474:
                listProduct.add(new ProductDTO(0, "Tovaglioli 25x25", "", UnitMeasure.PACCHI, "", 0, 2.17, "", ""));
                listProduct.add(new ProductDTO(0, "Buste nere 90x120", "", UnitMeasure.ALTRO, "cartone 20 KG", 0, 25.62, "", ""));
                listProduct.add(new ProductDTO(0, "Guanti s", "", UnitMeasure.UNITA, "", 0, 7.32, "", ""));
                listProduct.add(new ProductDTO(0, "Guanti m", "", UnitMeasure.UNITA, "", 0, 7.32, "", ""));
                listProduct.add(new ProductDTO(0, "Guanti L", "", UnitMeasure.UNITA, "", 0, 7.32, "", ""));
                listProduct.add(new ProductDTO(0, "Tovaglioli 24x24 12conf", "", UnitMeasure.CARTONI, "", 0, 26, "", ""));
                listProduct.add(new ProductDTO(0, "Tovaglioli 38x38 18conf", "", UnitMeasure.CARTONI, "", 0, 21.65, "", ""));
                listProduct.add(new ProductDTO(0, "Igenic floor 2 conf", "", UnitMeasure.CARTONI, "", 0, 11.72, "", ""));
                listProduct.add(new ProductDTO(0, "Carta formo", "", UnitMeasure.CARTONI, "", 0, 22.81, "", ""));
                listProduct.add(new ProductDTO(0, "Stovil power 15 lt", "", UnitMeasure.ALTRO, "bidone", 0, 39.65, "", ""));
                listProduct.add(new ProductDTO(0, "Rotolone 800str", "", UnitMeasure.ALTRO, "coppia", 0, 6.83, "", ""));
                listProduct.add(new ProductDTO(0, "Bicchieri 200cc ", "", UnitMeasure.UNITA, "", 4, 0.99, "", ""));
                listProduct.add(new ProductDTO(0, "Sgrassatore limone 750 ", "", UnitMeasure.UNITA, "", 22, 1.79, "", ""));
                listProduct.add(new ProductDTO(0, "Fornet 6UnitMeasure.KG", "", UnitMeasure.UNITA, "", 22, 16.16, "", ""));
                listProduct.add(new ProductDTO(0, "Cannucce h13 1000pz nere", "", UnitMeasure.UNITA, "", 22, 2.32, "", ""));
                listProduct.add(new ProductDTO(0, "Cannucce h21 nere", "", UnitMeasure.UNITA, "", 22, 6.34, "", ""));
                listProduct.add(new ProductDTO(0, "Fornet 750 ml", "", UnitMeasure.UNITA, "", 22, 2.93, "", ""));
                listProduct.add(new ProductDTO(0, "Spugne acciaio 25 pz", "", UnitMeasure.CARTONI, "", 22, 13, "", ""));
                listProduct.add(new ProductDTO(0, "Asciugamano 15 cnf", "", UnitMeasure.CARTONI, "", 22, 15.25, "", ""));
                listProduct.add(new ProductDTO(0, "Barchetta av 21,5x18x5", "", UnitMeasure.CARTONI, "", 22, 22.6, "", ""));
                listProduct.add(new ProductDTO(0, "Saniactive sgrassatore", "", UnitMeasure.UNITA, "", 22, 2.5, "", ""));
                listProduct.add(new ProductDTO(0, "Sapone liquido 3 lt", "", UnitMeasure.UNITA, "", 22, 4.76, "", ""));
                listProduct.add(new ProductDTO(0, "Spugna verde 10pz", "", UnitMeasure.CARTONI, "", 22, 5.86, "", ""));
                listProduct.add(new ProductDTO(0, "Panno blu vileda  10pz", "", UnitMeasure.CARTONI, "", 22, 3.48, "", ""));
                listProduct.add(new ProductDTO(0, "Spiedini 15 cm", "", UnitMeasure.CARTONI, "", 22, 6.22, "", ""));
                listProduct.add(new ProductDTO(0, "Spiedini 20cm", "", UnitMeasure.CARTONI, "", 22, 7.14, "", ""));
                listProduct.add(new ProductDTO(0, "Saccopoche 100pz", "", UnitMeasure.CARTONI, "", 22, 13.42, "", ""));
                listProduct.add(new ProductDTO(0, "Stuzzicadenti", "", UnitMeasure.CARTONI, "", 22, 3.9, "", ""));
                listProduct.add(new ProductDTO(0, "Acciaio lucidante", "", UnitMeasure.UNITA, "", 22, 5.67, "", ""));
                listProduct.add(new ProductDTO(0, "Sanialc", "", UnitMeasure.UNITA, "", 22, 1.9, "", ""));
                listProduct.add(new ProductDTO(0, "Buste goffrate 17x20 100pz", "", UnitMeasure.CARTONI, "", 22, 6.34, "", ""));
                listProduct.add(new ProductDTO(0, "Bicchiere 120cc", "", UnitMeasure.UNITA, "", 22, 1.88, "", ""));
                listProduct.add(new ProductDTO(0, "Brill power 15 lt", "", UnitMeasure.UNITA, "", 22, 42.09, "", ""));
                listProduct.add(new ProductDTO(0, "Panno giallo 5pz", "", UnitMeasure.CARTONI, "", 22, 9.76, "", ""));
                listProduct.add(new ProductDTO(0, "Bicchieri 25cc", "", UnitMeasure.UNITA, "", 22, 1.55, "", ""));
                listProduct.add(new ProductDTO(0, "Buste biocompostabili 90x120", "", UnitMeasure.CARTONI, "", 22, 33.37, "", ""));
                listProduct.add(new ProductDTO(0, "Candeggina 15 lt", "", UnitMeasure.CARTONI, "", 22, 6.22, "", ""));
                listProduct.add(new ProductDTO(0, "Shopper avano", "", UnitMeasure.UNITA, "", 22, 0.3, "", ""));
                listProduct.add(new ProductDTO(0, "Forchettine legno ", "", UnitMeasure.CARTONI, "", 22, 5.61, "", ""));
                listProduct.add(new ProductDTO(0, "Piatto asporto 50pz", "", UnitMeasure.UNITA, "", 22, 21.16, "", ""));
                listProduct.add(new ProductDTO(0, "Coperchio asporto ", "", UnitMeasure.UNITA, "", 22, 15.92, "", ""));
                listProduct.add(new ProductDTO(0, "Buste goffrate 20x30", "", UnitMeasure.CARTONI, "", 22, 9.58, "", ""));
                listProduct.add(new ProductDTO(0, "Buste ambra 42x28x85", "", UnitMeasure.CARTONI, "", 22, 42.7, "", ""));
                listProduct.add(new ProductDTO(0, "Rotolo alluminio 125mt", "", UnitMeasure.CARTONI, "", 22, 23.18, "", ""));
                listProduct.add(new ProductDTO(0, "Scatola hamburger 50pz", "", UnitMeasure.UNITA, "", 22, 11.47, "", ""));
                listProduct.add(new ProductDTO(0, "Barchetta av 20x12", "", UnitMeasure.UNITA, "", 22, 14.27, "", ""));
                listProduct.add(new ProductDTO(0, "Buste nere 90x120 20UnitMeasure.KG", "", UnitMeasure.CARTONI, "", 22, 25.62, "", ""));
                listProduct.add(new ProductDTO(0, "Rotolo pellicola 300 mt", "3 pz", UnitMeasure.CARTONI, "3 pz", 22, 17.87, "3 pz", ""));
                listProduct.add(new ProductDTO(0, "Geo piatti", "", UnitMeasure.UNITA, "", 22, 4.58, "", ""));
                break;
            case 475:
                listProduct.add(new ProductDTO(0, "Ca del bosco", "", UnitMeasure.LITRI, "", 0, 24.53, "", ""));
                listProduct.add(new ProductDTO(0, "Dompe luminor", "", UnitMeasure.LITRI, "", 0, 165, "", ""));
                listProduct.add(new ProductDTO(0, "Ghiaccio", "", UnitMeasure.CARTONI, "", 22, 7.2, "", ""));
                break;
            case 476:
                listProduct.add(new ProductDTO(0, "Lautant perrier", "", UnitMeasure.LITRI, "", 0, 32.15, "", ""));
                listProduct.add(new ProductDTO(0, "Laurant perrier rose", "", UnitMeasure.LITRI, "", 0, 54.3, "", ""));
                listProduct.add(new ProductDTO(0, "Calafuria", "", UnitMeasure.LITRI, "", 0, 8, "", ""));
                listProduct.add(new ProductDTO(0, "Chardonay", "", UnitMeasure.LITRI, "", 0, 5.26, "", ""));
                listProduct.add(new ProductDTO(0, "Fichimori", "", UnitMeasure.LITRI, "", 0, 6.47, "", ""));
                listProduct.add(new ProductDTO(0, "Perrier jouet", "", UnitMeasure.LITRI, "", 0, 42.5, "", ""));
                break;
            case 477:
                listProduct.add(new ProductDTO(0, "Tramari", "", UnitMeasure.LITRI, "", 0, 6.36, "", ""));
                listProduct.add(new ProductDTO(0, "Libol", "", UnitMeasure.LITRI, "", 0, 5.05, "", ""));
                listProduct.add(new ProductDTO(0, "Libel rose", "", UnitMeasure.LITRI, "", 0, 5.05, "", ""));
                listProduct.add(new ProductDTO(0, "Bellavista alma", "", UnitMeasure.LITRI, "", 0, 20.32, "", ""));
                break;
            case 478:
                listProduct.add(new ProductDTO(0, "Le rotaie", "", UnitMeasure.BOTTIGLIA, "", 0, 6.64, "", ""));
                listProduct.add(new ProductDTO(0, "Verso sud", "", UnitMeasure.BOTTIGLIA, "", 0, 6.64, "", ""));
                break;
            case 479:
                listProduct.add(new ProductDTO(0, "Baguette", "", UnitMeasure.UNITA, "", 0, 0.31, "", ""));
                listProduct.add(new ProductDTO(0, "Burger cereali", "", UnitMeasure.UNITA, "", 0, 0.34, "", ""));
                listProduct.add(new ProductDTO(0, " ", "", UnitMeasure.UNITA, "", 0, 0.28, "", ""));
                listProduct.add(new ProductDTO(0, "Panino hamburger", "", UnitMeasure.UNITA, "", 0, 0.43, "", ""));
                listProduct.add(new ProductDTO(0, "Club bianco", "", UnitMeasure.UNITA, "", 0, 0.33, "", ""));
                listProduct.add(new ProductDTO(0, "Club cereali", "", UnitMeasure.UNITA, "", 0, 0.38, "", ""));
                listProduct.add(new ProductDTO(0, "Taralli al primitivo", "", UnitMeasure.KG, "", 0, 7.2, "", ""));
                listProduct.add(new ProductDTO(0, "Pane casereccio", "", UnitMeasure.KG, "", 0, 2.4, "", ""));
                listProduct.add(new ProductDTO(0, "Panini hamburger mini", "", UnitMeasure.UNITA, "", 0, 0.18, "", ""));
                listProduct.add(new ProductDTO(0, "Frisa bianca", "", UnitMeasure.UNITA, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Frisa cereali", "", UnitMeasure.UNITA, "", 10, 0, "", ""));
                break;
            case 480:
                listProduct.add(new ProductDTO(0, "Hamburger vitello 180gr", "", UnitMeasure.UNITA, "", 0, 1.5, "", ""));
                listProduct.add(new ProductDTO(0, "Hamburger pollo 180gr", "", UnitMeasure.UNITA, "", 0, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Salsiccia sbudellata ", "", UnitMeasure.KG, "", 0, 7.9, "", ""));
                listProduct.add(new ProductDTO(0, "Tagliata di manzo", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Tartare di manzo ", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Filetto", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Mini hambuger", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Hamburger ripieno", "", UnitMeasure.PEZZI, "", 22, 0, "", ""));
                break;
            case 481:
                listProduct.add(new ProductDTO(0, "Controfiletto ", "", UnitMeasure.KG, "", 0, 8.14, "", ""));
                listProduct.add(new ProductDTO(0, "Petto intero", "", UnitMeasure.KG, "", 0, 8.64, "", ""));
                listProduct.add(new ProductDTO(0, "Petto senza osso", "", UnitMeasure.KG, "", 0, 9.25, "", ""));
                break;
            case 482:
                listProduct.add(new ProductDTO(0, "Capocollo", "", UnitMeasure.UNITA, "", 0, 35, "", ""));
                listProduct.add(new ProductDTO(0, "Filetto lardellato", "", UnitMeasure.UNITA, "", 0, 30, "", ""));
                listProduct.add(new ProductDTO(0, "Pancetta tesa", "", UnitMeasure.UNITA, "", 0, 30, "", ""));
                break;
            case 483:
                listProduct.add(new ProductDTO(0, "Formaggio cenere", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Formaggio nduja", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Formaggio finicchietto", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Caciocavallo ubriaco", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                break;
            case 484:
                listProduct.add(new ProductDTO(0, "Verdeca", "", UnitMeasure.BOTTIGLIA, "", 0, 4, "", ""));
                listProduct.add(new ProductDTO(0, "Nausica", "", UnitMeasure.BOTTIGLIA, "", 0, 5, "", ""));
                break;
            case 486:
                listProduct.add(new ProductDTO(0, "Clicquot", "", UnitMeasure.BOTTIGLIA, "", 0, 41.5, "", ""));
                listProduct.add(new ProductDTO(0, "Havana 7", "", UnitMeasure.BOTTIGLIA, "", 0, 25, "", ""));
                break;
            case 487:
                listProduct.add(new ProductDTO(0, "Rucola in mazzi", "", UnitMeasure.ALTRO, "mazzi", 0, 0.63, "", ""));
                listProduct.add(new ProductDTO(0, "Patate", "", UnitMeasure.KG, "", 0, 0.83, "", ""));
                listProduct.add(new ProductDTO(0, "Iceberg", "", UnitMeasure.UNITA, "", 0, 1.45, "", ""));
                listProduct.add(new ProductDTO(0, "Barbabietola rossa", "", UnitMeasure.UNITA, "", 0, 2.2, "", ""));
                listProduct.add(new ProductDTO(0, "Funghi champignon ", "", UnitMeasure.ALTRO, "vaschetta", 0, 1.3, "", ""));
                listProduct.add(new ProductDTO(0, "Mango", "", UnitMeasure.UNITA, "", 0, 1.77, "", ""));
                listProduct.add(new ProductDTO(0, "Zucchine", "", UnitMeasure.KG, "", 0, 1.14, "", ""));
                listProduct.add(new ProductDTO(0, "Cavolo cappuccio", "", UnitMeasure.UNITA, "", 0, 1.77, "", ""));
                listProduct.add(new ProductDTO(0, "Melanzane", "", UnitMeasure.KG, "", 0, 1.77, "", ""));
                listProduct.add(new ProductDTO(0, "Cipolla rossa", "", UnitMeasure.KG, "", 0, 1.77, "", ""));
                listProduct.add(new ProductDTO(0, "Noci sgusciate", "", UnitMeasure.KG, "", 0, 12.1, "", ""));
                listProduct.add(new ProductDTO(0, "Avocado", "", UnitMeasure.UNITA, "", 0, 1.77, "", ""));
                listProduct.add(new ProductDTO(0, "Limoni", "", UnitMeasure.KG, "", 0, 1.56, "", ""));
                listProduct.add(new ProductDTO(0, "Lime", "", UnitMeasure.KG, "", 0, 4.16, "", ""));
                listProduct.add(new ProductDTO(0, "Prezzemolo", "", UnitMeasure.UNITA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Finocchi", "", UnitMeasure.KG, "", 0, 1.24, "", ""));
                listProduct.add(new ProductDTO(0, "Pomodoro ramato", "", UnitMeasure.KG, "", 0, 1.56, "", ""));
                listProduct.add(new ProductDTO(0, "Datterino", "", UnitMeasure.KG, "", 0, 4.06, "", ""));
                listProduct.add(new ProductDTO(0, "Basilico", "", UnitMeasure.UNITA, "", 0, 1.77, "", ""));
                listProduct.add(new ProductDTO(0, "Timo", "", UnitMeasure.UNITA, "", 0, 1.77, "", ""));
                listProduct.add(new ProductDTO(0, "Granella pistacchio", "", UnitMeasure.KG, "", 0, 18.2, "", ""));
                listProduct.add(new ProductDTO(0, "Cetriolo", "", UnitMeasure.KG, "", 0, 1.56, "", ""));
                listProduct.add(new ProductDTO(0, "Salvia ", "", UnitMeasure.UNITA, "", 0, 1.77, "", ""));
                listProduct.add(new ProductDTO(0, "Carote", "", UnitMeasure.KG, "", 0, 1.04, "", ""));
                listProduct.add(new ProductDTO(0, "Arancia", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Lamelle di mandorle", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Funghi cardoncelli", "", UnitMeasure.KG, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Menta box", "", UnitMeasure.CARTONI, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Passion fruit", "", UnitMeasure.CARTONI, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Anacardi", "", UnitMeasure.KG, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Sesamo nero", "", UnitMeasure.KG, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Barattiere", "", UnitMeasure.UNITA, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Rosmarino pianta", "", UnitMeasure.UNITA, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Peperoni", "", UnitMeasure.UNITA, "", 22, 0, "", ""));
                break;
            case 488:
                listProduct.add(new ProductDTO(0, "Tonno rosso", "", UnitMeasure.UNITA, "", 0, 36, "", ""));
                listProduct.add(new ProductDTO(0, "Polpo ", "", UnitMeasure.UNITA, "", 0, 50, "", ""));
                listProduct.add(new ProductDTO(0, "Salmone affumicato", "", UnitMeasure.UNITA, "", 0, 21.35, "", ""));
                break;
            case 489:
                listProduct.add(new ProductDTO(0, "Acqua piccola ", "", UnitMeasure.FARDELLO, "", 0, 5.17, "", ""));
                listProduct.add(new ProductDTO(0, "Fanta ", "", UnitMeasure.FARDELLO, "", 0, 14, "", ""));
                listProduct.add(new ProductDTO(0, "Fusto rossa bock", "", UnitMeasure.ALTRO, "fusto", 0, 93.8, "", ""));
                listProduct.add(new ProductDTO(0, "Bombola c02", "", UnitMeasure.BOTTIGLIA, "", 0, 12.81, "", ""));
                listProduct.add(new ProductDTO(0, "Coca cola vap", "", UnitMeasure.FARDELLO, "", 0, 14, "", ""));
                listProduct.add(new ProductDTO(0, "Coca cola zero vap", "", UnitMeasure.FARDELLO, "", 0, 14, "", ""));
                listProduct.add(new ProductDTO(0, "Corona ", "", UnitMeasure.CT, "", 0, 25, "", ""));
                listProduct.add(new ProductDTO(0, "Ichnusa", "", UnitMeasure.CT, "", 0, 19.43, "", ""));
                listProduct.add(new ProductDTO(0, "Lemon soda vap", "", UnitMeasure.FARDELLO, "", 0, 10.21, "", ""));
                listProduct.add(new ProductDTO(0, "Menabrea ", "", UnitMeasure.CT, "", 0, 21.81, "", ""));
                listProduct.add(new ProductDTO(0, "Nastroc", "", UnitMeasure.CT, "", 0, 17.73, "", ""));
                listProduct.add(new ProductDTO(0, "Fusto pils ", "", UnitMeasure.ALTRO, "FUSTO", 0, 105.6, "", ""));
                listProduct.add(new ProductDTO(0, "Prosecco", "", UnitMeasure.CT, "", 0, 20.72, "", ""));
                listProduct.add(new ProductDTO(0, "Tennets", "", UnitMeasure.CT, "", 0, 31.65, "", ""));
                listProduct.add(new ProductDTO(0, "Fusto weise", "", UnitMeasure.ALTRO, "FUSTO", 0, 99.24, "", ""));
                listProduct.add(new ProductDTO(0, "Aperol", "", UnitMeasure.BOTTIGLIA, "", 0, 11.82, "", ""));
                listProduct.add(new ProductDTO(0, "Grappa bianca", "", UnitMeasure.BOTTIGLIA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Grappa barricata", "", UnitMeasure.BOTTIGLIA, "", 0, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Gin mare", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Liquirizia", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Amaro del capo", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Don papa", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Bayles", "", UnitMeasure.BOTTIGLIA, "", 22, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Tanqueray", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Limoncello", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Montenegro", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Jeghermaister", "", UnitMeasure.BOTTIGLIA, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Tonica vap", "", UnitMeasure.CARTONI, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Campari", "", UnitMeasure.BOTTIGLIA, "", 22, 15.92, "", ""));
                listProduct.add(new ProductDTO(0, "Bulldog", "", UnitMeasure.BOTTIGLIA, "", 22, 16.38, "", ""));
                listProduct.add(new ProductDTO(0, "Sky", "", UnitMeasure.BOTTIGLIA, "", 22, 11.63, "", ""));
                listProduct.add(new ProductDTO(0, "Tequila espolon blanco", "", UnitMeasure.BOTTIGLIA, "", 22, 17.64, "", ""));
                listProduct.add(new ProductDTO(0, "Bitter barbieri", "", UnitMeasure.BOTTIGLIA, "", 22, 9, "", ""));
                listProduct.add(new ProductDTO(0, "Cinzano rosso", "", UnitMeasure.BOTTIGLIA, "", 22, 8.49, "", ""));
                listProduct.add(new ProductDTO(0, "Red bull tonica", "", UnitMeasure.BOTTIGLIA, "", 22, 1.22, "", ""));
                listProduct.add(new ProductDTO(0, "Red bull Lemon", "", UnitMeasure.UNITA, "", 22, 1.22, "", ""));
                listProduct.add(new ProductDTO(0, "Limonata", "", UnitMeasure.CARTONI, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "The pesca", "", UnitMeasure.CARTONI, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "The limone", "", UnitMeasure.CARTONI, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Chinotto", "", UnitMeasure.CARTONI, "", 22, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Cedrata", "", UnitMeasure.CARTONI, "", 22, 0, "", ""));
                break;
            case 490:
                listProduct.add(new ProductDTO(0, "Olive denocciolate", "", UnitMeasure.BARATTOLIPICCOLI, "", 0, 1, "", ""));
                listProduct.add(new ProductDTO(0, "Conserva di peperoni piccanti", "", UnitMeasure.BARATTOLI, "", 0, 2.86, "", ""));
                listProduct.add(new ProductDTO(0, "Bacche vanilia", "", UnitMeasure.UNITA, "", 0, 5.06, "", ""));
                listProduct.add(new ProductDTO(0, "Formaggio fresco alpigiana", "", UnitMeasure.UNITA, "", 0, 5.15, "", ""));
                listProduct.add(new ProductDTO(0, "Taralli secchiello", "", UnitMeasure.SECCHIO, "", 0, 3.88, "", ""));
                listProduct.add(new ProductDTO(0, "Pepe nero", "", UnitMeasure.UNITA, "", 0, 7, "", ""));
                listProduct.add(new ProductDTO(0, "Farina caputo", "", UnitMeasure.UNITA, "", 0, 21.42, "", ""));
                listProduct.add(new ProductDTO(0, "Farina casillo ", "", UnitMeasure.UNITA, "", 0, 0.83, "", ""));
                listProduct.add(new ProductDTO(0, "Semola ", "", UnitMeasure.UNITA, "", 0, 0.99, "", ""));
                listProduct.add(new ProductDTO(0, "Tonno sott olio nostromo", "", UnitMeasure.UNITA, "", 0, 2, "", ""));
                listProduct.add(new ProductDTO(0, "Prosciutto cotto", "", UnitMeasure.UNITA, "", 0, 4.82, "", ""));
                listProduct.add(new ProductDTO(0, "Polpa di pomodoro", "", UnitMeasure.UNITA, "", 0, 7.69, "", ""));
                listProduct.add(new ProductDTO(0, "Olio extra vergine", "", UnitMeasure.UNITA, "", 0, 23.44, "", ""));
                listProduct.add(new ProductDTO(0, "Staccante spray", "", UnitMeasure.UNITA, "", 0, 5.61, "", ""));
                listProduct.add(new ProductDTO(0, "Olive nere 5UnitMeasure.KG", "", UnitMeasure.SECCHIO, "", 0, 4.11, "", ""));
                listProduct.add(new ProductDTO(0, "Passata di pomodoro divella", "", UnitMeasure.UNITA, "", 0, 0.64, "", ""));
                listProduct.add(new ProductDTO(0, "PatÃ¨ peperoncini", "", UnitMeasure.UNITA, "", 0, 5, "", ""));
                listProduct.add(new ProductDTO(0, "Mayonese bustine", "", UnitMeasure.CARTONI, "", 0, 7.7, "", ""));
                listProduct.add(new ProductDTO(0, "Pesto genovese", "", UnitMeasure.UNITA, "", 0, 9.15, "", ""));
                listProduct.add(new ProductDTO(0, "Ketchup bustine", "", UnitMeasure.CARTONI, "", 0, 5.8, "", ""));
                listProduct.add(new ProductDTO(0, "Mayonese 5UnitMeasure.KG", "", UnitMeasure.UNITA, "", 0, 12.77, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa tartara", "", UnitMeasure.BOTTIGLIA, "", 0, 5.49, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa bbq", "", UnitMeasure.BOTTIGLIA, "", 0, 4.5, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa pistacchio", "", UnitMeasure.BOTTIGLIA, "", 0, 14.54, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa boscaiola", "", UnitMeasure.BOTTIGLIA, "", 0, 6.22, "", ""));
                listProduct.add(new ProductDTO(0, "Mayonese brickl", "", UnitMeasure.BOTTIGLIA, "", 0, 6.22, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa bbq bustine", "", UnitMeasure.CARTONI, "", 0, 7.66, "", ""));
                listProduct.add(new ProductDTO(0, "Ketchup brick", "", UnitMeasure.BOTTIGLIA, "", 0, 2.75, "", ""));
                listProduct.add(new ProductDTO(0, "Senape brick", "", UnitMeasure.BOTTIGLIA, "", 0, 3.8, "", ""));
                listProduct.add(new ProductDTO(0, "Riso basmati 500gr", "", UnitMeasure.UNITA, "", 0, 2.07, "", ""));
                listProduct.add(new ProductDTO(0, "Riso venere 2UnitMeasure.KG", "", UnitMeasure.UNITA, "", 0, 7.49, "", ""));
                listProduct.add(new ProductDTO(0, "Orzo", "", UnitMeasure.UNITA, "", 0, 1.3, "", ""));
                listProduct.add(new ProductDTO(0, "Pan grattato  1UnitMeasure.KG", "", UnitMeasure.UNITA, "", 0, 1.5, "", ""));
                listProduct.add(new ProductDTO(0, "Prosciutto cotto altÃ  qualitÃ ", "", UnitMeasure.UNITA, "", 22, 7.86, "", ""));
                listProduct.add(new ProductDTO(0, "Petto arrosto", "", UnitMeasure.UNITA, "", 22, 9.5, "", ""));
                listProduct.add(new ProductDTO(0, "Panna oplÃ  500ml", "", UnitMeasure.UNITA, "", 22, 1.29, "", ""));
                listProduct.add(new ProductDTO(0, "Pomodori secchi", "", UnitMeasure.UNITA, "", 22, 10.56, "", ""));
                listProduct.add(new ProductDTO(0, "Funghi muschio", "", UnitMeasure.UNITA, "", 22, 15.85, "", ""));
                listProduct.add(new ProductDTO(0, "Kellogg,s corn flakes", "", UnitMeasure.UNITA, "", 22, 2.22, "", ""));
                listProduct.add(new ProductDTO(0, "Speck", "", UnitMeasure.UNITA, "", 22, 6.86, "", ""));
                listProduct.add(new ProductDTO(0, "Latte intero", "", UnitMeasure.UNITA, "", 22, 0.88, "", ""));
                listProduct.add(new ProductDTO(0, "Prosciutto Crudo di parma", "", UnitMeasure.UNITA, "", 22, 17.84, "", ""));
                listProduct.add(new ProductDTO(0, "Mortadella ", "", UnitMeasure.UNITA, "", 22, 9.5, "", ""));
                listProduct.add(new ProductDTO(0, "Salame spianata", "", UnitMeasure.UNITA, "", 22, 7.28, "", ""));
                listProduct.add(new ProductDTO(0, "Formaggio grattuggiato", "", UnitMeasure.UNITA, "", 22, 7.81, "", ""));
                listProduct.add(new ProductDTO(0, "Grana padano", "", UnitMeasure.UNITA, "", 22, 11.02, "", ""));
                listProduct.add(new ProductDTO(0, "Pan di stelle", "", UnitMeasure.UNITA, "", 22, 4.32, "", ""));
                listProduct.add(new ProductDTO(0, "Filetti di alici", "", UnitMeasure.UNITA, "", 10, 7.92, "", ""));
                listProduct.add(new ProductDTO(0, "Zucchero semolato", "", UnitMeasure.UNITA, "", 10, 0.95, "", ""));
                listProduct.add(new ProductDTO(0, "Nutella 3UnitMeasure.KG", "", UnitMeasure.UNITA, "", 10, 22.18, "", ""));
                listProduct.add(new ProductDTO(0, "Capperi", "", UnitMeasure.UNITA, "", 10, 4.22, "", ""));
                listProduct.add(new ProductDTO(0, "Livieti paneangeli", "", UnitMeasure.UNITA, "", 10, 2.95, "", ""));
                listProduct.add(new ProductDTO(0, "Pecorino", "", UnitMeasure.UNITA, "", 4, 8.88, "", ""));
                listProduct.add(new ProductDTO(0, "Olive leccino", "", UnitMeasure.UNITA, "", 10, 3.7, "", ""));
                listProduct.add(new ProductDTO(0, "Cacao amaro", "", UnitMeasure.UNITA, "", 10, 1.06, "", ""));
                listProduct.add(new ProductDTO(0, "Aceto di vino", "", UnitMeasure.UNITA, "", 10, 0.91, "", ""));
                listProduct.add(new ProductDTO(0, "Glassa ponti", "", UnitMeasure.UNITA, "", 10, 1.71, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa yogurt", "", UnitMeasure.UNITA, "", 10, 2.53, "", ""));
                listProduct.add(new ProductDTO(0, "Zucchero velo", "", UnitMeasure.UNITA, "", 10, 4.75, "", ""));
                listProduct.add(new ProductDTO(0, "Gelatina fogli", "", UnitMeasure.UNITA, "", 10, 0.95, "", ""));
                listProduct.add(new ProductDTO(0, "Crema tartufata", "", UnitMeasure.UNITA, "", 10, 11.62, "", ""));
                listProduct.add(new ProductDTO(0, "Cioccolato bianco", "", UnitMeasure.UNITA, "", 10, 14.26, "", ""));
                listProduct.add(new ProductDTO(0, "Formaggio quick", "", UnitMeasure.UNITA, "", 10, 0.59, "", ""));
                listProduct.add(new ProductDTO(0, "Olio di semi di girasoli 5lt", "", UnitMeasure.UNITA, "", 10, 17.42, "", ""));
                listProduct.add(new ProductDTO(0, "Rodez", "", UnitMeasure.UNITA, "", 4, 10.52, "", ""));
                listProduct.add(new ProductDTO(0, "Oro saiva", "", UnitMeasure.UNITA, "", 10, 3.64, "", ""));
                listProduct.add(new ProductDTO(0, "Sale grosso", "", UnitMeasure.UNITA, "", 22, 0.31, "", ""));
                listProduct.add(new ProductDTO(0, "Sale fino", "", UnitMeasure.UNITA, "", 22, 0.3, "", ""));
                listProduct.add(new ProductDTO(0, "Pancetta stufata", "", UnitMeasure.UNITA, "", 10, 5.7, "", ""));
                listProduct.add(new ProductDTO(0, "Frutti di bosco marmellata", "", UnitMeasure.UNITA, "", 10, 1.69, "", ""));
                listProduct.add(new ProductDTO(0, "Savoiardi", "", UnitMeasure.UNITA, "", 10, 1.37, "", ""));
                listProduct.add(new ProductDTO(0, "Wustel", "", UnitMeasure.UNITA, "", 10, 1.37, "", ""));
                listProduct.add(new ProductDTO(0, "Panna da cucina", "", UnitMeasure.UNITA, "", 10, 0.71, "", ""));
                listProduct.add(new ProductDTO(0, "Cioccolato fondente", "", UnitMeasure.UNITA, "", 10, 3.06, "", ""));
                listProduct.add(new ProductDTO(0, "Patate fry ", "", UnitMeasure.UNITA, "", 4, 2.45, "", ""));
                listProduct.add(new ProductDTO(0, "Cime di rapa", "", UnitMeasure.UNITA, "", 4, 4.19, "", ""));
                listProduct.add(new ProductDTO(0, "Salsa di soya", "", UnitMeasure.UNITA, "", 10, 2.85, "", ""));
                listProduct.add(new ProductDTO(0, "Topping caramello", "", UnitMeasure.UNITA, "", 10, 9.5, "", ""));
                listProduct.add(new ProductDTO(0, "Filetti di alici ", "", UnitMeasure.UNITA, "", 10, 7.66, "", ""));
                listProduct.add(new ProductDTO(0, "Pancetta scotennata", "", UnitMeasure.UNITA, "", 10, 8.87, "", ""));
                listProduct.add(new ProductDTO(0, "Gorgonzola", "", UnitMeasure.UNITA, "", 4, 7.44, "", ""));
                listProduct.add(new ProductDTO(0, "Burro 1UnitMeasure.KG", "", UnitMeasure.UNITA, "", 4, 7.6, "", ""));
                listProduct.add(new ProductDTO(0, "Mascarpone 250gr", "", UnitMeasure.UNITA, "", 4, 1.6, "", ""));
                listProduct.add(new ProductDTO(0, "Cheddar", "", UnitMeasure.UNITA, "", 4, 7.68, "", ""));
                listProduct.add(new ProductDTO(0, "Bresaola", "", UnitMeasure.KG, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Soppressata piccante", "", UnitMeasure.KG, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Bacon", "", UnitMeasure.UNITA, "", 22, 0, "", ""));
                break;
            case 491:
                listProduct.add(new ProductDTO(0, "Pistacchiello 12pz", "", UnitMeasure.CARTONI, "", 10, 22.57, "", ""));
                listProduct.add(new ProductDTO(0, "Tiramisu 12pz", "", UnitMeasure.CARTONI, "", 10, 27.72, "", ""));
                listProduct.add(new ProductDTO(0, "Cheesecake mirtilli 6pz", "", UnitMeasure.CARTONI, "", 10, 13.86, "", ""));
                listProduct.add(new ProductDTO(0, "Cheecake cioccolato 6pz", "", UnitMeasure.CARTONI, "", 10, 13.86, "", ""));
                listProduct.add(new ProductDTO(0, "Salame cacao", "", UnitMeasure.PEZZI, "", 10, 16.37, "", ""));
                listProduct.add(new ProductDTO(0, "Barattolo tre cioccolati", "", UnitMeasure.CARTONI, "", 10, 0, "", ""));
                listProduct.add(new ProductDTO(0, "Barattolo chantilly", "", UnitMeasure.CARTONI, "", 10, 0, "", ""));
                break;

        }
        return listProduct;
    }

    public static ProductDTO getProductInstance(String productName) {
        return ProductDTO.builder()
                .productId(0)
                .name(productName)
                .unitMeasure(UnitMeasure.KG)
                .vatApplied(4)
                .price(0)
                .productCode("")
                .description("")
                .category("")
                .sku("")
                .build();
    }

}
