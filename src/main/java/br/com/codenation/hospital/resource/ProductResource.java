package br.com.codenation.hospital.resource;

import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.services.HospitalService;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.codenation.hospital.constant.Constant;
import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.domain.Product;
import br.com.codenation.hospital.dto.ProductDTO;
import br.com.codenation.hospital.services.HospitalService;
import br.com.codenation.hospital.services.ProductService;
import br.com.codenation.hospital.services.exception.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/v1/hospitais")
public class ProductResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductResource.class);
	
	@Autowired

	private ProductService service;
	@Autowired
	private HospitalService hospitalService;

	private ProductService productService;
	
	@GetMapping(path="/{hospital_id}/estoque/{produto_id}")
	public ResponseEntity<ProductDTO> findProductBy(@PathVariable String hospital_id, @PathVariable String produto_id){
		try {
			 ProductDTO productDTO = productService.findById(produto_id);
				
			 return Optional
		            .ofNullable(productDTO)
		            .map(productReponse -> ResponseEntity.ok().body(productReponse))          
		            .orElseGet( () -> ResponseEntity.notFound().build() ); 
		} catch (Exception ex) {
			LOGGER.error("findProductBy - Handling error with message: {}", ex.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping(path="/{hospital_id}/estoque")
	public ResponseEntity<List<ProductDTO>> findAllProductBy(@PathVariable String hospital_id){
		try {
			 List<ProductDTO> productList = productService.findByHospitalId(hospital_id);
			
			 return Optional
		            .ofNullable(productList)
		            .map(productReponse -> ResponseEntity.ok().body(productReponse))          
		            .orElseGet( () -> ResponseEntity.notFound().build() ); 

		} catch (Exception ex) {
			LOGGER.error("findAllProductBy - Handling error with message: {}", ex.getMessage());
			
	        return ResponseEntity.badRequest().build();
		}
	}
	
	@PostMapping(path="/{hospital_id}/estoque")
	public ResponseEntity<ProductDTO> insert(@PathVariable String hospital_id, @RequestBody ProductDTO productDTO){
		try {		
			ProductDTO newProductDTO = productService.insert(hospital_id, productDTO);

			return Optional
		            .ofNullable(newProductDTO)
		            .map(productReponse -> ResponseEntity.ok().body(productReponse))          
		            .orElseGet( () -> ResponseEntity.notFound().build() ); 
		}
		catch (Exception ex) {
			LOGGER.error("insert - Handling error with message: {}", ex.getMessage());
			
	        return ResponseEntity.badRequest().build();
		}
	}
	
	@DeleteMapping(path="/{hospital_id}/estoque/{produto_id}")
	public ResponseEntity<String> delete(@PathVariable String hospital_id, @PathVariable String produto_id){
		try {
			ProductDTO deleteProductDTO = productService.findById(produto_id);
			
			if (deleteProductDTO != null) {
				productService.delete(hospital_id, deleteProductDTO.getId());
			}
			
			return Optional
		            .ofNullable(deleteProductDTO)
		            .map(productReponse -> ResponseEntity.ok().body("Produto apagado id: " + produto_id))          
		            .orElseGet( () -> ResponseEntity.notFound().build() ); 
			
		} catch (Exception ex) {
			LOGGER.error("delete - Handling error with message: {}", ex.getMessage());
			
	        return ResponseEntity.badRequest().build();
		}
	}
	
	@PutMapping(path="/{hospital_id}/estoque/{produto_id}")
	public ResponseEntity<ProductDTO> update(@RequestBody ProductDTO productDTO, @PathVariable String hospital_id, @PathVariable String produto_id){
		try {
			productDTO.setId(produto_id);
			
			ProductDTO updateProductDTO = productService.update(hospital_id, productDTO);
			
			return Optional
		            .ofNullable(updateProductDTO)
		            .map(hospitalReponse -> ResponseEntity.ok().body(hospitalReponse))          
		            .orElseGet( () -> ResponseEntity.notFound().build() ); 
			
		} catch (Exception ex) {
			LOGGER.error("update - Handling error with message: {}", ex.getMessage());
			
	        return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping(path="/{hospital_id}/estoque")
	public ResponseEntity<List<Product>> findByProducts(@PathVariable String hospital_id){
		try {
			Hospital obj = hospitalService.findById(hospital_id);

			if (obj != null) {
				List<Product> productList = obj.getProducts();

				return Optional
						.ofNullable(productList)
						.map(productResponse -> ResponseEntity.ok().body(productResponse))
						.orElseGet( () -> ResponseEntity.notFound().build() );
			}

			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			e.printStackTrace(); // TODO - trocar por logger

			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping(path="/{hospital_id}/estoque/{produto}")
	public ResponseEntity<Product> findByProductById(@PathVariable String hospital_id, @PathVariable String produto){
		try {
			Hospital obj = hospitalService.findById(hospital_id);

			if (obj != null) {
				List<Product> productList = obj.getProducts();
				Product product = null;

				if (productList.size() > 0) {
					product = productList
							.stream()
							.filter(productFilter -> productFilter.getId().trim().equals(produto))
							.findFirst()
							.orElse(null);
				}

				return Optional
						.ofNullable(product)
						.map(productResponse -> ResponseEntity.ok().body(productResponse))
						.orElseGet( () -> ResponseEntity.notFound().build() );
			}

			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			e.printStackTrace(); // TODO - trocar por logger

			return ResponseEntity.notFound().build();
		}
	}
}