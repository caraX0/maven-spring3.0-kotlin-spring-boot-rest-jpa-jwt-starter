package osahner.api.address

import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import osahner.service.CsvImportService
import osahner.service.PoiExportService
import java.util.*

@Component
class AddressService(
  private val addressRepository: AddressRepository,
  private val csvImportService: CsvImportService,
  private val poiExportService: PoiExportService
) {
  fun list(): Collection<Address> = addressRepository.findAll()

  fun findById(id: Int): Optional<Address> = addressRepository.findById(id)

  fun save(dto: AddressDto): Address = addressRepository.saveAndFlush(Address.fromDTO(dto))

  fun delete(id: Int) = addressRepository.deleteById(id)

  fun import(file: MultipartFile): Collection<Address> =
    csvImportService.import<AddressImportDto, Address>(file).also { addressRepository.saveAll(it) }

  fun export(): ResponseEntity<ByteArrayResource> {
    val result = addressRepository.findAll().map { it.toDTO() }
    val wb = poiExportService.buildExcelDocument(
      "Export Address List",
      listOf("id", "name", "street", "zip", "city", "email", "tel", "enabled", "things", "options", "lastModified"),
      result
    )
    return poiExportService.toResponseEntity(wb, "Address-List")
  }
}
