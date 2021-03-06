package br.com.codenation.hospital.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.domain.Product;
import br.com.codenation.hospital.dto.ProductDTO;
import br.com.codenation.hospital.repository.HospitalRepository;
import br.com.codenation.hospital.repository.ProductRepository;
import br.com.codenation.hospital.services.exception.ObjectNotFoundException;

@Service
public class ProductService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
	
	@Autowired
	private  ProductRepository productRepository;
	
	@Autowired
	private  HospitalRepository hospitalRepository;
	
	@Autowired
	private  HospitalService hospitalService;
	
	public List<ProductDTO> findAll(){
		return convertToDTOs(productRepository.findAll());
	}
	
	public ProductDTO findById(ObjectId id) {
		return convertToDTO(productRepository.findBy_id(id));
    }
	
	public ProductDTO findById(String id) {
		return convertToDTO(findProductById(id));
    }
	
	private Hospital findHospitalById(String id) {
        Optional<Hospital> result = hospitalRepository.findById(id);
		return result.orElseThrow(() -> new ObjectNotFoundException("Hospital não encontrado! ID: "+ id));
    }
	
	private Product findProductById(String id) {
        Optional<Product> result = productRepository.findById(id);
		return result.orElseThrow(() -> new ObjectNotFoundException("Product não encontrado! ID: "+ id));
    }
	
	public List<ProductDTO> findByHospitalId(String hospitalId) {
		Hospital hospital = hospitalService.findById(hospitalId);
	    List<Product> products = hospital.getProducts();
		return convertToDTOs(products);
    }
	
	public List<ProductDTO> findByName(String name) {
		List<Product> products = productRepository.findByNameLikeIgnoreCase(name);
		return convertToDTOs(products);
    }
	
	//Using Collection References
	public ProductDTO insert(String hospitalId, ProductDTO productDTO) {
	    Product product = fromDTO(productDTO);

	    product = productRepository.save(product);

		Hospital hospital = hospitalService.findById(hospitalId);
	    hospital.setProduct(product);
	    Hospital hospitalDb = hospitalRepository.save(hospital);
	    
		return convertToDTO(product);
	}
	
	//Using Collection References
	public void delete(String hospitalId, String productId) {
		Product removeProduct = findProductById(productId);

		Hospital hospital = hospitalService.findById(hospitalId);
	    hospital.getProducts().remove(removeProduct);

	    Hospital hospitalDb = hospitalService.update(hospital);
	    
		productRepository.deleteById(productId);
	}
	
	public ProductDTO update(String hospitalId, ProductDTO product) {
		Product updateProduct = findProductById(product.getId());
		updateProduct.setName(product.getName());
		updateProduct.setDescription(product.getDescription());
		updateProduct.setQuantity(product.getQuantity());
		updateProduct.setProductType(product.getProductType());
		
		return convertToDTO(productRepository.save(updateProduct));
	}
	

	public Product fromDTO(ProductDTO productDTO) {
		return new Product(productDTO.getProductName(), productDTO.getDescription(), productDTO.getQuantity(), productDTO.getProductType());
	}
	
	private ProductDTO convertToDTO(Product model) {
		ProductDTO dto = new ProductDTO();

        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setDescription(model.getDescription());
        dto.setQuantity(model.getQuantity());
        dto.setProductType(model.getProductType());
        
        return dto;
    }
	
	private List<ProductDTO> convertToDTOs(List<Product> models) {
        return models.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
	
	
}