using VinheriaAgnelloCRUD.DTOs.Wines;

namespace VinheriaAgnelloCRUD.Services;

public interface IWineService
{
    Task<IReadOnlyCollection<WineResponse>> GetAllAsync();
    Task<WineResponse?> GetByIdAsync(Guid id);
    Task<WineResponse> CreateAsync(CreateWineRequest request);
    Task<WineResponse?> UpdateAsync(Guid id, UpdateWineRequest request);
    Task<bool> DeleteAsync(Guid id);
}
