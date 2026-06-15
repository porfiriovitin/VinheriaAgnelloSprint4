using Microsoft.EntityFrameworkCore;
using VinheriaAgnelloCRUD.Data;
using VinheriaAgnelloCRUD.DTOs.Wines;
using VinheriaAgnelloCRUD.Mappings;

namespace VinheriaAgnelloCRUD.Services;

public class WineService : IWineService
{
    private readonly AppDbContext _context;

    public WineService(AppDbContext context)
    {
        _context = context;
    }

    public async Task<IReadOnlyCollection<WineResponse>> GetAllAsync()
    {
        return await _context.Wines
            .AsNoTracking()
            .OrderBy(w => w.Name)
            .Select(w => w.ToResponse())
            .ToListAsync();
    }

    public async Task<WineResponse?> GetByIdAsync(Guid id)
    {
        var wine = await _context.Wines
            .AsNoTracking()
            .FirstOrDefaultAsync(w => w.Id == id);

        return wine?.ToResponse();
    }

    public async Task<WineResponse> CreateAsync(CreateWineRequest request)
    {
        var wine = request.ToEntity();

        _context.Wines.Add(wine);
        await _context.SaveChangesAsync();

        return wine.ToResponse();
    }

    public async Task<WineResponse?> UpdateAsync(Guid id, UpdateWineRequest request)
    {
        var wine = await _context.Wines.FindAsync(id);

        if (wine is null)
            return null;

        wine.ApplyUpdate(request);
        await _context.SaveChangesAsync();

        return wine.ToResponse();
    }

    public async Task<bool> DeleteAsync(Guid id)
    {
        var wine = await _context.Wines.FindAsync(id);

        if (wine is null)
            return false;

        _context.Wines.Remove(wine);
        await _context.SaveChangesAsync();

        return true;
    }
}
